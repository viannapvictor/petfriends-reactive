package com.petfriends.almoxarifado.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.almoxarifado.events.EstoqueReservado;
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
        EstoqueReservado evento = new EstoqueReservado();
        String eventJson = "{\"id\":\"RES-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("RES-001")
                .aggregateType("ReservaEstoque")
                .eventType("EstoqueReservado")
                .eventData(eventJson)
                .version(1L)
                .timestamp(LocalDateTime.now())
                .build();

        when(repository.findMaxVersionByAggregateId("RES-001")).thenReturn(Mono.just(0L));
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("RES-001", "ReservaEstoque", evento))
                .expectNextMatches(entry ->
                        entry.getAggregateId().equals("RES-001") &&
                        entry.getVersion().equals(1L) &&
                        entry.getEventType().equals("EstoqueReservado")
                )
                .verifyComplete();

        verify(repository).findMaxVersionByAggregateId("RES-001");
        verify(objectMapper).writeValueAsString(evento);
        verify(repository).save(any(EventStoreEntry.class));
    }

    @Test
    @DisplayName("Deve adicionar evento com versão incremental")
    void deveAdicionarEventoComVersaoIncremental() throws JsonProcessingException {
        EstoqueReservado evento = new EstoqueReservado();
        String eventJson = "{\"id\":\"RES-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("RES-001")
                .version(3L)
                .build();

        when(repository.findMaxVersionByAggregateId("RES-001")).thenReturn(Mono.just(2L));
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("RES-001", "ReservaEstoque", evento))
                .expectNextMatches(entry -> entry.getVersion().equals(3L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve adicionar primeiro evento quando não existe versão anterior")
    void deveAdicionarPrimeiroEventoQuandoNaoExisteVersaoAnterior() throws JsonProcessingException {
        EstoqueReservado evento = new EstoqueReservado();
        String eventJson = "{\"id\":\"RES-001\"}";

        EventStoreEntry savedEntry = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("RES-001")
                .version(1L)
                .build();

        when(repository.findMaxVersionByAggregateId("RES-001")).thenReturn(Mono.empty());
        when(objectMapper.writeValueAsString(evento)).thenReturn(eventJson);
        when(repository.save(any(EventStoreEntry.class))).thenReturn(Mono.just(savedEntry));

        StepVerifier.create(service.appendEvent("RES-001", "ReservaEstoque", evento))
                .expectNextMatches(entry -> entry.getVersion().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar erro ao falhar serialização")
    void deveRetornarErroAoFalharSerializacao() throws JsonProcessingException {
        EstoqueReservado evento = new EstoqueReservado();

        when(repository.findMaxVersionByAggregateId("RES-001")).thenReturn(Mono.just(0L));
        when(objectMapper.writeValueAsString(evento)).thenThrow(new JsonProcessingException("Erro de serialização") {});

        StepVerifier.create(service.appendEvent("RES-001", "ReservaEstoque", evento))
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
                .aggregateId("RES-001")
                .version(1L)
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .id(UUID.randomUUID())
                .aggregateId("RES-001")
                .version(2L)
                .build();

        when(repository.findByAggregateIdOrderByVersionAsc("RES-001"))
                .thenReturn(Flux.just(entry1, entry2));

        StepVerifier.create(service.loadEvents("RES-001"))
                .expectNext(entry1)
                .expectNext(entry2)
                .verifyComplete();

        verify(repository).findByAggregateIdOrderByVersionAsc("RES-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há eventos")
    void deveRetornarVazioQuandoNaoHaEventos() {
        when(repository.findByAggregateIdOrderByVersionAsc("RES-999"))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.loadEvents("RES-999"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve desserializar evento com sucesso")
    void deveDesserializarEventoComSucesso() throws Exception {
        EventStoreEntry entry = EventStoreEntry.builder()
                .eventData("{\"id\":\"RES-001\"}")
                .eventType("EstoqueReservado")
                .build();

        EstoqueReservado evento = new EstoqueReservado();

        when(objectMapper.readValue("{\"id\":\"RES-001\"}", EstoqueReservado.class))
                .thenReturn(evento);

        StepVerifier.create(service.deserializeEvent(entry, EstoqueReservado.class))
                .expectNext(evento)
                .verifyComplete();

        verify(objectMapper).readValue("{\"id\":\"RES-001\"}", EstoqueReservado.class);
    }

    @Test
    @DisplayName("Deve retornar erro ao falhar desserialização")
    void deveRetornarErroAoFalharDesserializacao() throws Exception {
        EventStoreEntry entry = EventStoreEntry.builder()
                .eventData("{invalid json}")
                .eventType("EstoqueReservado")
                .build();

        when(objectMapper.readValue(anyString(), eq(EstoqueReservado.class)))
                .thenThrow(new JsonProcessingException("Erro de desserialização") {});

        StepVerifier.create(service.deserializeEvent(entry, EstoqueReservado.class))
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
                .eventData("{\"id\":\"RES-001\"}")
                .build();

        EventStoreEntry entry2 = EventStoreEntry.builder()
                .eventData("{\"id\":\"RES-002\"}")
                .build();

        EstoqueReservado evento1 = new EstoqueReservado();
        EstoqueReservado evento2 = new EstoqueReservado();

        when(repository.findByAggregateIdOrderByVersionAsc("RES-001"))
                .thenReturn(Flux.just(entry1, entry2));
        when(objectMapper.readValue(anyString(), eq(EstoqueReservado.class)))
                .thenReturn(evento1, evento2);

        StepVerifier.create(service.replayEvents("RES-001", EstoqueReservado.class))
                .expectNext(evento1)
                .expectNext(evento2)
                .verifyComplete();
    }
}

