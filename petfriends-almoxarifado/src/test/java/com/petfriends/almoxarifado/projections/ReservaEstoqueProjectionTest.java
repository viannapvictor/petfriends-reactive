package com.petfriends.almoxarifado.projections;

import com.petfriends.almoxarifado.domain.Endereco;
import com.petfriends.almoxarifado.events.*;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueItemView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueItemViewRepository;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueViewRepository;
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
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaEstoqueProjection")
class ReservaEstoqueProjectionTest {

    @Mock
    private ReservaEstoqueViewRepository viewRepository;

    @Mock
    private ReservaEstoqueItemViewRepository itemViewRepository;

    @InjectMocks
    private ReservaEstoqueProjection projection;

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");
    }

    @Test
    @DisplayName("Deve processar evento EstoqueReservado com sucesso")
    void deveProcessarEventoEstoqueReservado() {
        // Arrange
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10),
                new EstoqueReservado.ItemReservado("PROD-002", 5)
        );
        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", endereco, itens);

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("PENDENTE")
                .build();

        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));
        when(itemViewRepository.save(any(ReservaEstoqueItemView.class)))
                .thenReturn(Mono.just(new ReservaEstoqueItemView()));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).save(any(ReservaEstoqueView.class));
        verify(itemViewRepository, times(2)).save(any(ReservaEstoqueItemView.class));
    }

    @Test
    @DisplayName("Deve processar evento EstoqueInsuficiente com sucesso")
    void deveProcessarEventoEstoqueInsuficiente() {
        // Arrange
        EstoqueInsuficiente evento = new EstoqueInsuficiente("RES-001", "PED-001", Arrays.asList("PROD-001"), "Produto PROD-001 sem estoque");

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("INSUFICIENTE")
                .build();

        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).save(any(ReservaEstoqueView.class));
    }

    @Test
    @DisplayName("Deve processar evento ReservaConfirmada com sucesso")
    void deveProcessarEventoReservaConfirmada() {
        // Arrange
        ReservaConfirmada evento = new ReservaConfirmada("RES-001", "PED-001");

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("PENDENTE")
                .build();

        when(viewRepository.findById("RES-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("RES-001");
        verify(viewRepository).save(any(ReservaEstoqueView.class));
    }

    @Test
    @DisplayName("Deve processar evento ReservaCancelada com sucesso")
    void deveProcessarEventoReservaCancelada() {
        // Arrange
        ReservaCancelada evento = new ReservaCancelada("RES-001", "PED-001", "Cliente desistiu");

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .status("PENDENTE")
                .build();

        when(viewRepository.findById("RES-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("RES-001");
        verify(viewRepository).save(any(ReservaEstoqueView.class));
    }

    @Test
    @DisplayName("Deve processar evento ItensSeparados com sucesso")
    void deveProcessarEventoItensSeparados() {
        // Arrange
        ItensSeparados evento = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001");

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .status("CONFIRMADA")
                .build();

        when(viewRepository.findById("RES-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("RES-001");
        verify(viewRepository).save(any(ReservaEstoqueView.class));
    }

    @Test
    @DisplayName("Deve processar múltiplos eventos em sequência")
    void deveProcessarMultiplosEventosEmSequencia() {
        // Arrange
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10)
        );
        EstoqueReservado evento1 = new EstoqueReservado("RES-001", "PED-001", endereco, itens);
        ReservaConfirmada evento2 = new ReservaConfirmada("RES-001", "PED-001");
        ItensSeparados evento3 = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001");

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("PENDENTE")
                .build();

        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));
        when(itemViewRepository.save(any(ReservaEstoqueItemView.class)))
                .thenReturn(Mono.just(new ReservaEstoqueItemView()));
        when(viewRepository.findById("RES-001"))
                .thenReturn(Mono.just(view));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento1, evento2, evento3)))
                .verifyComplete();

        verify(viewRepository, times(3)).save(any(ReservaEstoqueView.class));
        verify(viewRepository, times(2)).findById("RES-001");
        verify(itemViewRepository).save(any(ReservaEstoqueItemView.class));
    }

    @Test
    @DisplayName("Deve ignorar evento desconhecido")
    void deveIgnorarEventoDesconhecido() {
        // Arrange
        BaseEvent<?> eventoDesconhecido = new BaseEvent<>("RES-001") {};

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(eventoDesconhecido)))
                .verifyComplete();

        verifyNoInteractions(viewRepository);
        verifyNoInteractions(itemViewRepository);
    }

    @Test
    @DisplayName("Deve tratar erro ao salvar view")
    void deveTratarErroAoSalvarView() {
        // Arrange
        EstoqueInsuficiente evento = new EstoqueInsuficiente("RES-001", "PED-001", Arrays.asList("PROD-001"), "Produto PROD-001 sem estoque");

        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao salvar no banco")));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).save(any(ReservaEstoqueView.class));
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar view existente")
    void deveTratarErroAoBuscarViewExistente() {
        // Arrange
        ReservaConfirmada evento = new ReservaConfirmada("RES-001", "PED-001");

        when(viewRepository.findById(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findById("RES-001");
    }

    @Test
    @DisplayName("Deve processar EstoqueReservado com endereco nulo")
    void deveProcessarEstoqueReservadoComEnderecoNulo() {
        // Arrange
        List<EstoqueReservado.ItemReservado> itens = Arrays.asList(
                new EstoqueReservado.ItemReservado("PROD-001", 10)
        );
        EstoqueReservado evento = new EstoqueReservado("RES-001", "PED-001", null, itens);

        ReservaEstoqueView view = ReservaEstoqueView.builder()
                .id("RES-001")
                .pedidoId("PED-001")
                .status("PENDENTE")
                .build();

        when(viewRepository.save(any(ReservaEstoqueView.class)))
                .thenReturn(Mono.just(view));
        when(itemViewRepository.save(any(ReservaEstoqueItemView.class)))
                .thenReturn(Mono.just(new ReservaEstoqueItemView()));

        // Act
        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        // Assert
        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).save(any(ReservaEstoqueView.class));
        verify(itemViewRepository).save(any(ReservaEstoqueItemView.class));
    }
}