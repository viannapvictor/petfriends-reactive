package com.petfriends.transporte.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EventPublisher")
class EventPublisherTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private EventPublisher eventPublisher;

    private EntregaAgendada evento;

    @BeforeEach
    void setUp() {
        evento = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", 
                "Rua A, 100", LocalDate.of(2025, 12, 25)
        );
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
    @DisplayName("Deve publicar evento de TransporteIniciado")
    void devePublicarEventoDeTransporteIniciado() {
        TransporteIniciado eventoIniciado = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoIniciado))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoIniciado));
    }

    @Test
    @DisplayName("Deve publicar evento de EntregaConcluida")
    void devePublicarEventoDeEntregaConcluida() {
        LocalDateTime dataRecebimento = LocalDateTime.now();
        EntregaConcluida eventoConcluida = new EntregaConcluida(
                "ENT-001", "PED-001", "João Silva", dataRecebimento, "OK"
        );

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoConcluida))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoConcluida));
    }

    @Test
    @DisplayName("Deve publicar evento de EntregaDevolvida")
    void devePublicarEventoDeEntregaDevolvida() {
        LocalDateTime dataDevolucao = LocalDateTime.now();
        EntregaDevolvida eventoDevolvida = new EntregaDevolvida(
                "ENT-001", "PED-001", "Cliente ausente", dataDevolucao, "MOT-001"
        );

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoDevolvida))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoDevolvida));
    }

    @Test
    @DisplayName("Deve publicar evento de EntregaExtraviada")
    void devePublicarEventoDeEntregaExtraviada() {
        LocalDateTime dataExtravio = LocalDateTime.now();
        EntregaExtraviada eventoExtraviada = new EntregaExtraviada(
                "ENT-001", "PED-001", "Perdido", dataExtravio, "Centro"
        );

        when(streamBridge.send(eq("output-events"), any(BaseEvent.class)))
                .thenReturn(true);

        StepVerifier.create(eventPublisher.publish(eventoExtraviada))
                .verifyComplete();

        verify(streamBridge).send(eq("output-events"), eq(eventoExtraviada));
    }
}

