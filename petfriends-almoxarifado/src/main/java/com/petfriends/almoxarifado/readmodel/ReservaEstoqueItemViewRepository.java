package com.petfriends.almoxarifado.readmodel;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ReservaEstoqueItemViewRepository extends ReactiveCrudRepository<ReservaEstoqueItemView, UUID> {
    
    @Query("SELECT * FROM reserva_estoque_item_view WHERE reserva_id = :reservaId")
    Flux<ReservaEstoqueItemView> findByReservaId(String reservaId);
    
    @Query("DELETE FROM reserva_estoque_item_view WHERE reserva_id = :reservaId")
    Mono<Void> deleteByReservaId(String reservaId);
}

