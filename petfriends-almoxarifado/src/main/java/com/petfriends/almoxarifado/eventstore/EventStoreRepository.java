package com.petfriends.almoxarifado.eventstore;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface EventStoreRepository extends ReactiveCrudRepository<EventStoreEntry, UUID> {
    
    /**
     * Carrega todos os eventos de um agregado específico, ordenados por versão
     */
    @Query("SELECT * FROM event_store WHERE aggregate_id = :aggregateId ORDER BY version ASC")
    Flux<EventStoreEntry> findByAggregateIdOrderByVersionAsc(String aggregateId);
    
    /**
     * Obtém a última versão de um agregado
     */
    @Query("SELECT COALESCE(MAX(version), 0) FROM event_store WHERE aggregate_id = :aggregateId")
    Mono<Long> findMaxVersionByAggregateId(String aggregateId);
    
    /**
     * Verifica se já existe evento com a versão especificada (para optimistic locking)
     */
    @Query("SELECT COUNT(*) > 0 FROM event_store WHERE aggregate_id = :aggregateId AND version = :version")
    Mono<Boolean> existsByAggregateIdAndVersion(String aggregateId, Long version);
}

