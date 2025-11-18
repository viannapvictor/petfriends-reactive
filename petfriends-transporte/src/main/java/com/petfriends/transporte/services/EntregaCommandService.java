package com.petfriends.transporte.services;

import com.petfriends.transporte.commands.AgendarEntregaCommand;
import reactor.core.publisher.Mono;

/**
 * Service de Comandos - CQRS Command Side
 */
public interface EntregaCommandService {
    
    /**
     * Agenda uma entrega
     */
    Mono<String> agendarEntrega(String pedidoId, String reservaId, 
                                 AgendarEntregaCommand.EnderecoDTO endereco, 
                                 String dataPrevisaoEntrega);
    
    /**
     * Inicia o transporte de uma entrega
     */
    Mono<String> iniciarTransporte(String id, String motoristaId, String veiculoId);
    
    /**
     * Conclui uma entrega
     */
    Mono<String> concluirEntrega(String id, String recebedor, 
                                  String dataRecebimento, String observacoes);
}
