package com.petfriends.transporte.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.transporte.events.EntregaAgendada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EventStoreService")
class EventStoreServiceTest {

    @Mock
    private EventStoreRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private EventStoreService service;

    @BeforeEach
    void setUp() {
        service = new EventStoreService(repository, objectMapper);
    }

    @Test
    @DisplayName("Deve adicionar evento com sucesso")
    void deveAdicionarEventoComSucesso() throws JsonProcessingException {
        EntregaAgendada evento = new EntregaAgendada();
        String eventJson = "{\"id\":\"ENT-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("ENT-001")
                .aggregateType("Entrega")
                .eventType("EntregaAgendada")
                .eventData(eventJson)
                .version(1L)
                .timestamp(LocalDateTime.now())
                .build();

        when(repository.findMaxVersionByAggregateId("ENT-001")).thenReturn(Mono.just(0L));
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("ENT-001", "Entrega", evento))
                .expectNextMatches(entry ->
                        entry.getAggregateId().equals("ENT-001") &&
                        entry.getVersion().equals(1L) &&
                        entry.getEventType().equals("EntregaAgendada")
                )
                .verifyComplete();

        verify(repository).findMaxVersionByAggregateId("ENT-001");
        verify(objectMapper).writeValueAsString(evento);
        verify(repository).save(any(EventStoreEntry.class));
    }

    @Test
    @DisplayName("Deve adicionar evento com versão incremental")
    void deveAdicionarEventoComVersaoIncremental() throws JsonProcessingException {
        EntregaAgendada evento = new EntregaAgendada();
        String eventJson = "{\"id\":\"ENT-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("ENT-001")
                .version(3L)
                .build();

        when(repository.findMaxVersionByAggregateId("ENT-001")).thenReturn(Mono.just(2L));
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("ENT-001", "Entrega", evento))
                .expectNextMatches(entry -> entry.getVersion().equals(3L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve adicionar primeiro evento quando não existe versão anterior")
    void deveAdicionarPrimeiroEventoQuandoNaoExisteVersaoAnterior() throws JsonProcessingException {
        EntregaAgendada evento = new EntregaAgendada();
        String eventJson = "{\"id\":\"ENT-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("ENT-001")
                .version(1L)
                .build();

        when(repository.findMaxVersionByAggregateId("ENT-001")).thenReturn(Mono.empty());
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("ENT-001", "Entrega", evento))
                .expectNextMatches(entry -> entry.getVersion().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar erro ao falhar serialização")
    void deveRetornarErroAoFalharSerializacao() throws JsonProcessingException {
        EntregaAgendada evento = new EntregaAgendada();

        when(repository.findMaxVersionByAggregateId("ENT-001")).thenReturn(Mono.just(0L));
        when(objectMapper.writeValueAsString(evento)).thenThrow(new JsonProcessingException("Erro de serialização") {});

        StepVerifier.create(service.appendEvent("ENT-001", "Entrega", evento))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                        error.getMessage().contains("Failed to serialize event")
                )
                .verify();
    }

    @Test
    @DisplayName("Deve carregar eventos por aggregateId")
    void deveCarregarEventosPorAggregateId() {
        EventStoreEntry entry1 = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("ENT-001")
                .version(1L)
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("ENT-001")
                .version(2L)
                .build();

        when(repository.findByAggregateIdOrderByVersionAsc("ENT-001"))
                .thenReturn(Flux.just(entry1, entry2));

        StepVerifier.create(service.loadEvents("ENT-001"))
                .expectNext(entry1)
                .expectNext(entry2)
                .verifyComplete();

        verify(repository).findByAggregateIdOrderByVersionAsc("ENT-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há eventos")
    void deveRetornarVazioQuandoNaoHaEventos() {
        when(repository.findByAggregateIdOrderByVersionAsc("ENT-999"))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.loadEvents("ENT-999"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve desserializar evento com sucesso")
    void deveDesserializarEventoComSucesso() throws Exception {
        EventStoreEntry entry = EventStoreEntry.builder()
                .eventData("{\"id\":\"ENT-001\"}")
                .eventType("EntregaAgendada")
                .build();

        EntregaAgendada evento = new EntregaAgendada();

        when(objectMapper.readValue("{\"id\":\"ENT-001\"}", EntregaAgendada.class))
                .thenReturn(evento);

        StepVerifier.create(service.deserializeEvent(entry, EntregaAgendada.class))
                .expectNext(evento)
                .verifyComplete();

        verify(objectMapper).readValue("{\"id\":\"ENT-001\"}", EntregaAgendada.class);
    }

    @Test
    @DisplayName("Deve retornar erro ao falhar desserialização")
    void deveRetornarErroAoFalharDesserializacao() throws Exception {
        EventStoreEntry entry = EventStoreEntry.builder()
                .eventData("{invalid json}")
                .eventType("EntregaAgendada")
                .build();

        when(objectMapper.readValue(anyString(), eq(EntregaAgendada.class)))
                .thenThrow(new JsonProcessingException("Erro de desserialização") {});

        StepVerifier.create(service.deserializeEvent(entry, EntregaAgendada.class))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                        error.getMessage().contains("Failed to deserialize event")
                )
                .verify();
    }

    @Test
    @DisplayName("Deve fazer replay de eventos")
    void deveFazerReplayDeEventos() throws Exception {
        EventStoreEntry entry1 = EventStoreEntry.builder()
                .eventData("{\"id\":\"ENT-001\"}")
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .eventData("{\"id\":\"ENT-002\"}")
                .build();

        EntregaAgendada evento1 = new EntregaAgendada();
        EntregaAgendada evento2 = new EntregaAgendada();

        when(repository.findByAggregateIdOrderByVersionAsc("ENT-001"))
                .thenReturn(Flux.just(entry1, entry2));
        when(objectMapper.readValue(anyString(), eq(EntregaAgendada.class)))
                .thenReturn(evento1, evento2);

        StepVerifier.create(service.replayEvents("ENT-001", EntregaAgendada.class))
                .expectNext(evento1)
                .expectNext(evento2)
                .verifyComplete();
    }
}

