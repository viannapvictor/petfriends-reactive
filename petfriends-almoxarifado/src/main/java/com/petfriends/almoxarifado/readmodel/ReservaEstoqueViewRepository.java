package com.petfriends.almoxarifado.readmodel;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReservaEstoqueViewRepository extends ReactiveCrudRepository<ReservaEstoqueView, String> {
    
    @Query("SELECT * FROM reserva_estoque_view WHERE pedido_id = :pedidoId")
    Mono<ReservaEstoqueView> findByPedidoId(String pedidoId);
}

