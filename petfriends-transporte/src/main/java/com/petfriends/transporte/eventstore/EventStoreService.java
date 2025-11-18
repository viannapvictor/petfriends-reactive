package com.petfriends.transporte.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.transporte.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStoreService {
    
    private final EventStoreRepository repository;
    private final ObjectMapper objectMapper;
    
    public <T extends BaseEvent<?>> Mono<EventStoreEntry> appendEvent(
            String aggregateId, 
            String aggregateType,
            T event) {
        
        return repository.findMaxVersionByAggregateId(aggregateId)
            .defaultIfEmpty(0L)
            .flatMap(currentVersion -> {
                long nextVersion = currentVersion + 1;
                
                try {
                    String eventData = objectMapper.writeValueAsString(event);
                    
                    EventStoreEntry entry = EventStoreEntry.builder()
                        .id(UUID.randomUUID())
                        .aggregateId(aggregateId)
                        .aggregateType(aggregateType)
                        .eventType(event.getClass().getSimpleName())
                        .eventData(eventData)
                        .version(nextVersion)
                        .timestamp(LocalDateTime.now())
                        .build();
                    
                    return repository.save(entry)
                        .doOnSuccess(saved -> log.info(
                            "Event stored: aggregateId={}, type={}, version={}", 
                            aggregateId, event.getClass().getSimpleName(), nextVersion))
                        .doOnError(error -> log.error(
                            "Failed to store event: aggregateId={}, type={}", 
                            aggregateId, event.getClass().getSimpleName(), error));
                    
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Failed to serialize event", e));
                }
            });
    }
    
    public Flux<EventStoreEntry> loadEvents(String aggregateId) {
        return repository.findByAggregateIdOrderByVersionAsc(aggregateId)
            .doOnComplete(() -> log.debug("Loaded events for aggregateId={}", aggregateId));
    }
    
    public <T> Mono<T> deserializeEvent(EventStoreEntry entry, Class<T> eventClass) {
        return Mono.fromCallable(() -> 
            objectMapper.readValue(entry.getEventData(), eventClass)
        ).onErrorResume(e -> {
            log.error("Failed to deserialize event: type={}", entry.getEventType(), e);
            return Mono.error(new RuntimeException("Failed to deserialize event", e));
        });
    }
    
    public <T> Flux<T> replayEvents(String aggregateId, Class<T> eventClass) {
        return loadEvents(aggregateId)
            .flatMap(entry -> deserializeEvent(entry, eventClass));
    }
}

