package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import reactor.core.publisher.Mono;

public interface ReservaEstoqueQueryService {
    
    Mono<ReservaEstoqueView> obterPorId(String id);
    
    Mono<ReservaEstoqueView> obterPorPedidoId(String pedidoId);
}
