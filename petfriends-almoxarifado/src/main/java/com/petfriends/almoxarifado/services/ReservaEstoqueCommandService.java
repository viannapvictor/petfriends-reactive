package com.petfriends.almoxarifado.services;

import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service de Comandos - CQRS Command Side
 */
public interface ReservaEstoqueCommandService {
    
    /**
     * Reserva estoque para um pedido
     */
    Mono<String> reservarEstoque(String pedidoId, List<ItemReservaRequest> itens);
    
    /**
     * Confirma uma reserva
     */
    Mono<String> confirmarReserva(String id);
    
    /**
     * Cancela uma reserva
     */
    Mono<String> cancelarReserva(String id, String motivo);
    
    /**
     * Separa itens de uma reserva
     */
    Mono<String> separarItens(String id, String operadorId);
    
    /**
     * DTO para Item de Reserva
     */
    @Data
    class ItemReservaRequest {
        public String produtoId;
        public int quantidade;
    }
}
