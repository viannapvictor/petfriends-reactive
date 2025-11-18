package com.petfriends.transporte.eventstore;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface EventStoreRepository extends ReactiveCrudRepository<EventStoreEntry, UUID> {
    
    @Query("SELECT * FROM event_store WHERE aggregate_id = :aggregateId ORDER BY version ASC")
    Flux<EventStoreEntry> findByAggregateIdOrderByVersionAsc(String aggregateId);
    
    @Query("SELECT COALESCE(MAX(version), 0) FROM event_store WHERE aggregate_id = :aggregateId")
    Mono<Long> findMaxVersionByAggregateId(String aggregateId);
    
    @Query("SELECT COUNT(*) > 0 FROM event_store WHERE aggregate_id = :aggregateId AND version = :version")
    Mono<Boolean> existsByAggregateIdAndVersion(String aggregateId, Long version);
}

