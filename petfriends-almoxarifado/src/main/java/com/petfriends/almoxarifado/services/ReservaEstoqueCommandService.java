package com.petfriends.almoxarifado.services;

import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReservaEstoqueCommandService {
    
    Mono<String> reservarEstoque(String pedidoId, List<ItemReservaRequest> itens);
    
    Mono<String> confirmarReserva(String id);
    
    Mono<String> cancelarReserva(String id, String motivo);
    
    Mono<String> separarItens(String id, String operadorId);
    
    @Data
    class ItemReservaRequest {
        public String produtoId;
        public int quantidade;
    }
}
