package com.petfriends.almoxarifado.events;

import com.petfriends.almoxarifado.domain.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EventPublisher")
class EventPublisherTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private EventPublisher eventPublisher;

    private EstoqueReservado evento;

    @BeforeEach
    void setUp() {
        Endereco endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "SP", "SP", "01000-000");
        EstoqueReservado.ItemReservado item = new EstoqueReservado.ItemReservado("PROD-001", 10);
        evento = new EstoqueReservado("RES-001", "PED-001", endereco, Arrays.asList(item));
    }

    @Test
    @DisplayName("Deve publicar evento com sucesso")
    void devePublicarEventoComSucesso() {
        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(evento))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(evento));
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao publicar evento")
    void deveLancarExcecaoQuandoFalhaAoPublicarEvento() {
        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(false);

        StepVerifier.create(eventPublisher.publish(evento))
                .expectError(RuntimeException.class)
                .verify();

        verify(streamBridge).send(eq("output-events"), eq(evento));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro ao enviar")
    void deveLancarExcecaoQuandoOcorreErroAoEnviar() {
        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenThrow(new RuntimeException("Erro no Kafka"));

        StepVerifier.create(eventPublisher.publish(evento))
                .expectError(RuntimeException.class)
                .verify();

        verify(streamBridge).send(eq("output-events"), eq(evento));
    }

    @Test
    @DisplayName("Deve publicar evento de ReservaConfirmada")
    void devePublicarEventoDeReservaConfirmada() {
        ReservaConfirmada eventoConfirmada = new ReservaConfirmada("RES-001", "PED-001");

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoConfirmada))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoConfirmada));
    }

    @Test
    @DisplayName("Deve publicar evento de ReservaCancelada")
    void devePublicarEventoDeReservaCancelada() {
        ReservaCancelada eventoCancelada = new ReservaCancelada("RES-001", "PED-001", "Cliente cancelou");

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoCancelada))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoCancelada));
    }

    @Test
    @DisplayName("Deve publicar evento de ItensSeparados")
    void devePublicarEventoDeItensSeparados() {
        Endereco endereco = new Endereco("Rua A", "100", null, "Centro", "SP", "SP", "01000-000");
        ItensSeparados eventoSeparados = new ItensSeparados("RES-001", "PED-001", endereco, "OP-001");

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoSeparados))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoSeparados));
    }

    @Test
    @DisplayName("Deve publicar evento de EstoqueInsuficiente")
    void devePublicarEventoDeEstoqueInsuficiente() {
        EstoqueInsuficiente eventoInsuficiente = new EstoqueInsuficiente(
                "RES-001", "PED-001", Arrays.asList("PROD-001"), "Sem estoque"
        );

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoInsuficiente))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoInsuficiente));
    }
}

