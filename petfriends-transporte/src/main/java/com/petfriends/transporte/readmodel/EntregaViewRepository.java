package com.petfriends.transporte.readmodel;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EntregaViewRepository extends ReactiveCrudRepository<EntregaView, String> {
    
    /**
     * Busca entrega por pedidoId
     */
    @Query("SELECT * FROM entrega_view WHERE pedido_id = :pedidoId")
    Mono<EntregaView> findByPedidoId(String pedidoId);
    
    /**
     * Busca entrega por reservaId
     */
    @Query("SELECT * FROM entrega_view WHERE reserva_id = :reservaId")
    Mono<EntregaView> findByReservaId(String reservaId);
    
    /**
     * Busca entregas por motorista
     */
    @Query("SELECT * FROM entrega_view WHERE motorista_id = :motoristaId ORDER BY created_at DESC")
    Flux<EntregaView> findByMotoristaId(String motoristaId);
    
    /**
     * Busca entregas por status
     */
    @Query("SELECT * FROM entrega_view WHERE status = :status ORDER BY created_at DESC")
    Flux<EntregaView> findByStatus(String status);
}

