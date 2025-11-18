package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import reactor.core.publisher.Mono;

/**
 * Service de Queries - CQRS Query Side
 */
public interface ReservaEstoqueQueryService {
    
    /**
     * Obtém reserva por ID
     */
    Mono<ReservaEstoqueView> obterPorId(String id);
    
    /**
     * Obtém reserva por Pedido ID
     */
    Mono<ReservaEstoqueView> obterPorPedidoId(String pedidoId);
}
