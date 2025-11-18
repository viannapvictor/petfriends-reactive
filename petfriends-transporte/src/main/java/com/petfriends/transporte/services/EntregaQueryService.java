package com.petfriends.transporte.services;

import com.petfriends.transporte.readmodel.EntregaView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service de Queries - CQRS Query Side
 */
public interface EntregaQueryService {
    
    /**
     * Obtém entrega por ID
     */
    Mono<EntregaView> obterPorId(String id);
    
    /**
     * Obtém entrega por Pedido ID
     */
    Mono<EntregaView> obterPorPedidoId(String pedidoId);
    
    /**
     * Obtém entrega por Reserva ID
     */
    Mono<EntregaView> obterPorReservaId(String reservaId);
    
    /**
     * Lista entregas de um motorista
     */
    Flux<EntregaView> listarPorMotorista(String motoristaId);
    
    /**
     * Lista entregas por status
     */
    Flux<EntregaView> listarPorStatus(String status);
}
