package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.domain.Endereco;
import com.petfriends.almoxarifado.events.*;
import com.petfriends.almoxarifado.eventstore.EventStoreEntry;
import com.petfriends.almoxarifado.eventstore.EventStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaEstoqueCommandServiceImpl")
class ReservaEstoqueCommandServiceImplTest {

    @Mock
    private EventStoreService eventStoreService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ReservaEstoqueCommandServiceImpl service;

    private ReservaEstoqueCommandService.EnderecoRequest enderecoRequest;
    private List<ReservaEstoqueCommandService.ItemReservaRequest> itensRequest;

    @BeforeEach
    void setUp() {
        enderecoRequest = new ReservaEstoqueCommandService.EnderecoRequest();
        enderecoRequest.rua = "Rua A";
        enderecoRequest.numero = "100";
        enderecoRequest.complemento = "Apt 10";
        enderecoRequest.bairro = "Centro";
        enderecoRequest.cidade = "São Paulo";
        enderecoRequest.estado = "SP";
        enderecoRequest.cep = "01000-000";

        ReservaEstoqueCommandService.ItemReservaRequest item1 = new ReservaEstoqueCommandService.ItemReservaRequest();
        item1.produtoId = "PROD-001";
        item1.quantidade = 10;

        ReservaEstoqueCommandService.ItemReservaRequest item2 = new ReservaEstoqueCommandService.ItemReservaRequest();
        item2.produtoId = "PROD-002";
        item2.quantidade = 5;

        itensRequest = Arrays.asList(item1, item2);
    }

    @Test
    @DisplayName("Deve reservar estoque com sucesso")
    void deveReservarEstoqueComSucesso() {
        // Arrange
        EventStoreEntry entry = new EventStoreEntry();
        when(eventStoreService.appendEvent(anyString(), eq("ReservaEstoque"), any(BaseEvent.class)))
                .thenReturn(Mono.just(entry));
        when(eventPublisher.publish(any(BaseEvent.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.reservarEstoque("PED-001", enderecoRequest, itensRequest))
                .expectNextMatches(reservaId -> reservaId != null && !reservaId.isEmpty())
                .verifyComplete();

        verify(eventStoreService).appendEvent(anyString(), eq("ReservaEstoque"), any(EstoqueReservado.class));
        verify(eventPublisher).publish(any(EstoqueReservado.class));
    }

    @Test
    @DisplayName("Deve confirmar reserva com sucesso")
    void deveConfirmarReservaComSucesso() {
        // Arrange
        String reservaId = "RES-001";
        Endereco endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");

        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10)
        );
        EstoqueReservado estoqueReservado = new EstoqueReservado(reservaId, "PED-001", endereco, itens);

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EstoqueReservado");

        when(eventStoreService.loadEvents(reservaId))
                .thenReturn(Flux.just(entry1));
        when(eventStoreService.deserializeEvent(any(EventStoreEntry.class), eq(EstoqueReservado.class)))
                .thenReturn(Mono.just(estoqueReservado));
        when(eventStoreService.appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ReservaConfirmada.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(ReservaConfirmada.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.confirmarReserva(reservaId))
                .expectNext(reservaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(reservaId);
        verify(eventStoreService).appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ReservaConfirmada.class));
        verify(eventPublisher).publish(any(ReservaConfirmada.class));
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso")
    void deveCancelarReservaComSucesso() {
        // Arrange
        String reservaId = "RES-001";
        String motivo = "Cliente desistiu";
        Endereco endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");

        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10)
        );
        EstoqueReservado estoqueReservado = new EstoqueReservado(reservaId, "PED-001", endereco, itens);

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EstoqueReservado");

        when(eventStoreService.loadEvents(reservaId))
                .thenReturn(Flux.just(entry1));
        when(eventStoreService.deserializeEvent(any(EventStoreEntry.class), eq(EstoqueReservado.class)))
                .thenReturn(Mono.just(estoqueReservado));
        when(eventStoreService.appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ReservaCancelada.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(ReservaCancelada.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.cancelarReserva(reservaId, motivo))
                .expectNext(reservaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(reservaId);
        verify(eventStoreService).appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ReservaCancelada.class));
        verify(eventPublisher).publish(any(ReservaCancelada.class));
    }

    @Test
    @DisplayName("Deve separar itens com sucesso")
    void deveSepararItensComSucesso() {
        // Arrange
        String reservaId = "RES-001";
        String operadorId = "OP-001";
        Endereco endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");

        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10)
        );
        EstoqueReservado estoqueReservado = new EstoqueReservado(reservaId, "PED-001", endereco, itens);
        ReservaConfirmada reservaConfirmada = new ReservaConfirmada(reservaId, "PED-001");

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EstoqueReservado");
        EventStoreEntry entry2 = new EventStoreEntry();
        entry2.setEventType("ReservaConfirmada");

        when(eventStoreService.loadEvents(reservaId))
                .thenReturn(Flux.just(entry1, entry2));
        when(eventStoreService.deserializeEvent(eq(entry1), eq(EstoqueReservado.class)))
                .thenReturn(Mono.just(estoqueReservado));
        when(eventStoreService.deserializeEvent(eq(entry2), eq(ReservaConfirmada.class)))
                .thenReturn(Mono.just(reservaConfirmada));
        when(eventStoreService.appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ItensSeparados.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(ItensSeparados.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.separarItens(reservaId, operadorId))
                .expectNext(reservaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(reservaId);
        verify(eventStoreService).appendEvent(eq(reservaId), eq("ReservaEstoque"), any(ItensSeparados.class));
        verify(eventPublisher).publish(any(ItensSeparados.class));
    }

    @Test
    @DisplayName("Deve lançar erro quando tipo de evento é desconhecido")
    void deveLancarErroQuandoTipoEventoDesconhecido() {
        // Arrange
        String reservaId = "RES-001";
        EventStoreEntry entry = new EventStoreEntry();
        entry.setEventType("EventoDesconhecido");

        when(eventStoreService.loadEvents(reservaId))
                .thenReturn(Flux.just(entry));
        lenient().when(eventStoreService.deserializeEvent(eq(entry), any()))
                .thenThrow(new IllegalArgumentException("Unknown event type: EventoDesconhecido"));

        // Act & Assert
        StepVerifier.create(service.confirmarReserva(reservaId))
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("Deve tratar erro ao reservar estoque")
    void deveTratarErroAoReservarEstoque() {
        // Arrange
        when(eventStoreService.appendEvent(anyString(), eq("ReservaEstoque"), any(BaseEvent.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao salvar evento")));

        // Act & Assert
        StepVerifier.create(service.reservarEstoque("PED-001", enderecoRequest, itensRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve tratar erro ao publicar evento")
    void deveTratarErroAoPublicarEvento() {
        // Arrange
        EventStoreEntry entry = new EventStoreEntry();
        when(eventStoreService.appendEvent(anyString(), eq("ReservaEstoque"), any(BaseEvent.class)))
                .thenReturn(Mono.just(entry));
        when(eventPublisher.publish(any(BaseEvent.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao publicar evento")));

        // Act & Assert
        StepVerifier.create(service.reservarEstoque("PED-001", enderecoRequest, itensRequest))
                .expectError(RuntimeException.class)
                .verify();
    }
}