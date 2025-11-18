package com.petfriends.transporte.controllers;

import com.petfriends.transporte.commands.AgendarEntregaCommand;
import com.petfriends.transporte.services.EntregaCommandService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controller de Comandos (CQRS - Command Side)
 * Operações que modificam estado
 */
@RestController
@RequestMapping("/transporte/entregas")
@RequiredArgsConstructor
public class EntregaCommandController {

    private final EntregaCommandService service;

    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> agendarEntrega(
            @RequestBody AgendarEntregaRequest request) {
        return service.agendarEntrega(
                request.pedidoId, 
                request.reservaId, 
                request.endereco, 
                request.dataPrevisaoEntrega)
            .map(entregaId -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("entregaId", entregaId, "message", "Entrega agendada com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @PutMapping("/{id}/iniciar")
    public Mono<ResponseEntity<Map<String, String>>> iniciarTransporte(
            @PathVariable String id,
            @RequestBody IniciarTransporteRequest request) {
        return service.iniciarTransporte(id, request.motoristaId, request.veiculoId)
            .map(entregaId -> ResponseEntity.ok(
                Map.of("entregaId", entregaId, "message", "Transporte iniciado com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @PutMapping("/{id}/concluir")
    public Mono<ResponseEntity<Map<String, String>>> concluirEntrega(
            @PathVariable String id,
            @RequestBody ConcluirEntregaRequest request) {
        return service.concluirEntrega(id, request.recebedor, 
                                        request.dataRecebimento, 
                                        request.observacoes)
            .map(entregaId -> ResponseEntity.ok(
                Map.of("entregaId", entregaId, "message", "Entrega concluída com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @Data
    public static class AgendarEntregaRequest {
        private String pedidoId;
        private String reservaId;
        private AgendarEntregaCommand.EnderecoDTO endereco;
        private String dataPrevisaoEntrega;
    }

    @Data
    public static class IniciarTransporteRequest {
        private String motoristaId;
        private String veiculoId;
    }

    @Data
    public static class ConcluirEntregaRequest {
        private String recebedor;
        private String dataRecebimento;
        private String observacoes;
    }
}
