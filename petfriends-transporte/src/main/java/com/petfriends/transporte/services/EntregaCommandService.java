package com.petfriends.transporte.services;

import com.petfriends.transporte.commands.AgendarEntregaCommand;
import reactor.core.publisher.Mono;

public interface EntregaCommandService {
    
    Mono<String> agendarEntrega(String pedidoId, String reservaId, 
                                 AgendarEntregaCommand.EnderecoDTO endereco, 
                                 String dataPrevisaoEntrega);
    
    Mono<String> iniciarTransporte(String id, String motoristaId, String veiculoId);
    
    Mono<String> concluirEntrega(String id, String recebedor, 
                                  String dataRecebimento, String observacoes);
}
