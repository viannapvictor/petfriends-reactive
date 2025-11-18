package com.petfriends.transporte.services;

import com.petfriends.transporte.readmodel.EntregaView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EntregaQueryService {
    
    Mono<EntregaView> obterPorId(String id);
    
    Mono<EntregaView> obterPorPedidoId(String pedidoId);
    
    Mono<EntregaView> obterPorReservaId(String reservaId);
    
    Flux<EntregaView> listarPorMotorista(String motoristaId);
    
    Flux<EntregaView> listarPorStatus(String status);
}
