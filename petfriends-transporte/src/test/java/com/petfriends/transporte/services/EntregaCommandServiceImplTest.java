package com.petfriends.transporte.services;

import com.petfriends.transporte.commands.AgendarEntregaCommand;
import com.petfriends.transporte.domain.Endereco;
import com.petfriends.transporte.events.*;
import com.petfriends.transporte.eventstore.EventStoreEntry;
import com.petfriends.transporte.eventstore.EventStoreService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EntregaCommandServiceImpl")
class EntregaCommandServiceImplTest {

    @Mock
    private EventStoreService eventStoreService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private EntregaCommandServiceImpl service;

    private AgendarEntregaCommand.EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        enderecoDTO = new AgendarEntregaCommand.EnderecoDTO(
                "Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000"
        );
    }

    @Test
    @DisplayName("Deve agendar entrega com sucesso")
    void deveAgendarEntregaComSucesso() {
        // Arrange
        EventStoreEntry entry = new EventStoreEntry();
        when(eventStoreService.appendEvent(anyString(), eq("Entrega"), any(BaseEvent.class)))
                .thenReturn(Mono.just(entry));
        when(eventPublisher.publish(any(BaseEvent.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.agendarEntrega("PED-001", "RES-001", enderecoDTO, "2025-12-01"))
                .expectNextMatches(entregaId -> entregaId != null && !entregaId.isEmpty())
                .verifyComplete();

        verify(eventStoreService).appendEvent(anyString(), eq("Entrega"), any(EntregaAgendada.class));
        verify(eventPublisher).publish(any(EntregaAgendada.class));
    }

    @Test
    @DisplayName("Deve iniciar transporte com sucesso")
    void deveIniciarTransporteComSucesso() {
        // Arrange
        String entregaId = "ENT-001";
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada entregaAgendada = new EntregaAgendada(
                entregaId, "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EntregaAgendada");

        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.just(entry1));
        when(eventStoreService.deserializeEvent(any(EventStoreEntry.class), eq(EntregaAgendada.class)))
                .thenReturn(Mono.just(entregaAgendada));
        when(eventStoreService.appendEvent(eq(entregaId), eq("Entrega"), any(TransporteIniciado.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(TransporteIniciado.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.iniciarTransporte(entregaId, "MOT-001", "VEI-001"))
                .expectNext(entregaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(entregaId);
        verify(eventStoreService).appendEvent(eq(entregaId), eq("Entrega"), any(TransporteIniciado.class));
        verify(eventPublisher).publish(any(TransporteIniciado.class));
    }

    @Test
    @DisplayName("Deve concluir entrega com sucesso")
    void deveConcluirEntregaComSucesso() {
        // Arrange
        String entregaId = "ENT-001";
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada entregaAgendada = new EntregaAgendada(
                entregaId, "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );
        TransporteIniciado transporteIniciado = new TransporteIniciado(entregaId, "MOT-001", "VEI-001");

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EntregaAgendada");
        EventStoreEntry entry2 = new EventStoreEntry();
        entry2.setEventType("TransporteIniciado");

        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.just(entry1, entry2));
        when(eventStoreService.deserializeEvent(eq(entry1), eq(EntregaAgendada.class)))
                .thenReturn(Mono.just(entregaAgendada));
        when(eventStoreService.deserializeEvent(eq(entry2), eq(TransporteIniciado.class)))
                .thenReturn(Mono.just(transporteIniciado));
        when(eventStoreService.appendEvent(eq(entregaId), eq("Entrega"), any(EntregaConcluida.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(EntregaConcluida.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.concluirEntrega(entregaId, "Cliente", "2025-12-01T14:30:00", "Tudo OK"))
                .expectNext(entregaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(entregaId);
        verify(eventStoreService).appendEvent(eq(entregaId), eq("Entrega"), any(EntregaConcluida.class));
        verify(eventPublisher).publish(any(EntregaConcluida.class));
    }

    @Test
    @DisplayName("Deve devolver entrega com sucesso")
    void deveDevolverEntregaComSucesso() {
        // Arrange
        String entregaId = "ENT-001";
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada entregaAgendada = new EntregaAgendada(
                entregaId, "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );
        TransporteIniciado transporteIniciado = new TransporteIniciado(entregaId, "MOT-001", "VEI-001");
        EntregaConcluida entregaConcluida = new EntregaConcluida(
                entregaId, "PED-001", "João Silva", LocalDateTime.of(2025, 12, 1, 10, 0), "Entregue com sucesso"
        );

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EntregaAgendada");
        EventStoreEntry entry2 = new EventStoreEntry();
        entry2.setEventType("TransporteIniciado");
        EventStoreEntry entry3 = new EventStoreEntry();
        entry3.setEventType("EntregaConcluida");

        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.just(entry1, entry2, entry3));
        when(eventStoreService.deserializeEvent(eq(entry1), eq(EntregaAgendada.class)))
                .thenReturn(Mono.just(entregaAgendada));
        when(eventStoreService.deserializeEvent(eq(entry2), eq(TransporteIniciado.class)))
                .thenReturn(Mono.just(transporteIniciado));
        when(eventStoreService.deserializeEvent(eq(entry3), eq(EntregaConcluida.class)))
                .thenReturn(Mono.just(entregaConcluida));
        when(eventStoreService.appendEvent(eq(entregaId), eq("Entrega"), any(EntregaDevolvida.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(EntregaDevolvida.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.devolverEntrega(entregaId, "Cliente ausente", "2025-12-01T14:30:00", "MOT-001"))
                .expectNext(entregaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(entregaId);
        verify(eventStoreService).appendEvent(eq(entregaId), eq("Entrega"), any(EntregaDevolvida.class));
        verify(eventPublisher).publish(any(EntregaDevolvida.class));
    }

    @Test
    @DisplayName("Deve marcar entrega como extraviada com sucesso")
    void deveMarcarEntregaExtraviadaComSucesso() {
        // Arrange
        String entregaId = "ENT-001";
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada entregaAgendada = new EntregaAgendada(
                entregaId, "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );
        TransporteIniciado transporteIniciado = new TransporteIniciado(entregaId, "MOT-001", "VEI-001");

        EventStoreEntry entry1 = new EventStoreEntry();
        entry1.setEventType("EntregaAgendada");
        EventStoreEntry entry2 = new EventStoreEntry();
        entry2.setEventType("TransporteIniciado");

        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.just(entry1, entry2));
        when(eventStoreService.deserializeEvent(eq(entry1), eq(EntregaAgendada.class)))
                .thenReturn(Mono.just(entregaAgendada));
        when(eventStoreService.deserializeEvent(eq(entry2), eq(TransporteIniciado.class)))
                .thenReturn(Mono.just(transporteIniciado));
        when(eventStoreService.appendEvent(eq(entregaId), eq("Entrega"), any(EntregaExtraviada.class)))
                .thenReturn(Mono.just(new EventStoreEntry()));
        when(eventPublisher.publish(any(EntregaExtraviada.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.marcarExtraviada(entregaId, "Pacote perdido", "2025-12-01T14:30:00", "Centro de Distribuição"))
                .expectNext(entregaId)
                .verifyComplete();

        verify(eventStoreService).loadEvents(entregaId);
        verify(eventStoreService).appendEvent(eq(entregaId), eq("Entrega"), any(EntregaExtraviada.class));
        verify(eventPublisher).publish(any(EntregaExtraviada.class));
    }

    @Test
    @DisplayName("Deve lançar erro quando tipo de evento é desconhecido")
    void deveLancarErroQuandoTipoEventoDesconhecido() {
        // Arrange
        String entregaId = "ENT-001";
        EventStoreEntry entry = new EventStoreEntry();
        entry.setEventType("EventoDesconhecido");

        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.just(entry));
        lenient().when(eventStoreService.deserializeEvent(any(EventStoreEntry.class), any()))
                .thenThrow(new IllegalArgumentException("Unknown event type: EventoDesconhecido"));

        // Act & Assert
        StepVerifier.create(service.iniciarTransporte(entregaId, "MOT-001", "VEI-001"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve tratar erro ao agendar entrega")
    void deveTratarErroAoAgendarEntrega() {
        // Arrange
        when(eventStoreService.appendEvent(anyString(), eq("Entrega"), any(BaseEvent.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao salvar evento")));

        // Act & Assert
        StepVerifier.create(service.agendarEntrega("PED-001", "RES-001", enderecoDTO, "2025-12-01"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve tratar erro ao publicar evento")
    void deveTratarErroAoPublicarEvento() {
        // Arrange
        EventStoreEntry entry = new EventStoreEntry();
        when(eventStoreService.appendEvent(anyString(), eq("Entrega"), any(BaseEvent.class)))
                .thenReturn(Mono.just(entry));
        when(eventPublisher.publish(any(BaseEvent.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao publicar evento")));

        // Act & Assert
        StepVerifier.create(service.agendarEntrega("PED-001", "RES-001", enderecoDTO, "2025-12-01"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve tratar erro ao reconstituir agregado")
    void deveTratarErroAoReconstituirAgregado() {
        // Arrange
        String entregaId = "ENT-001";
        when(eventStoreService.loadEvents(entregaId))
                .thenReturn(Flux.error(new RuntimeException("Erro ao carregar eventos")));

        // Act & Assert
        StepVerifier.create(service.iniciarTransporte(entregaId, "MOT-001", "VEI-001"))
                .expectError(RuntimeException.class)
                .verify();
    }
}