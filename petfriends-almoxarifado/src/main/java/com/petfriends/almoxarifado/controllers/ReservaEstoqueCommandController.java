package com.petfriends.almoxarifado.controllers;

import com.petfriends.almoxarifado.services.ReservaEstoqueCommandService;
import com.petfriends.almoxarifado.services.ReservaEstoqueCommandService.ItemReservaRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/almoxarifado/reservas")
@RequiredArgsConstructor
public class ReservaEstoqueCommandController {

    private final ReservaEstoqueCommandService service;

    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> reservarEstoque(
            @RequestBody ReservaRequest request) {
        return service.reservarEstoque(request.pedidoId, request.itens)
            .map(reservaId -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("reservaId", reservaId, "message", "Estoque reservado com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @PutMapping("/{id}/confirmar")
    public Mono<ResponseEntity<Map<String, String>>> confirmarReserva(
            @PathVariable String id) {
        return service.confirmarReserva(id)
            .map(reservaId -> ResponseEntity.ok(
                Map.of("reservaId", reservaId, "message", "Reserva confirmada com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @PutMapping("/cancelar")
    public Mono<ResponseEntity<Map<String, String>>> cancelarReserva(
            @RequestBody CancelarRequest request) {
        return service.cancelarReserva(request.id, request.motivo)
            .map(reservaId -> ResponseEntity.ok(
                Map.of("reservaId", reservaId, "message", "Reserva cancelada com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @PutMapping("/separar")
    public Mono<ResponseEntity<Map<String, String>>> separarItens(
            @RequestBody SepararRequest request) {
        return service.separarItens(request.id, request.operadorId)
            .map(reservaId -> ResponseEntity.ok(
                Map.of("reservaId", reservaId, "message", "Itens separados com sucesso")))
            .onErrorResume(error -> Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", error.getMessage()))));
    }

    @Data
    public static class ReservaRequest {
        private String pedidoId;
        private List<ItemReservaRequest> itens;
    }

    @Data
    public static class CancelarRequest {
        private String id;
        private String motivo;
    }

    @Data
    public static class SepararRequest {
        private String id;
        private String operadorId;
    }
}
