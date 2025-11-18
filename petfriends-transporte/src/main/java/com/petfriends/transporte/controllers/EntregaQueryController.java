package com.petfriends.transporte.controllers;

import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.services.EntregaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transporte/entregas")
@RequiredArgsConstructor
public class EntregaQueryController {

    private final EntregaQueryService service;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntregaView>> obterPorId(@PathVariable String id) {
        return service.obterPorId(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public Mono<ResponseEntity<EntregaView>> obterPorPedidoId(@PathVariable String pedidoId) {
        return service.obterPorPedidoId(pedidoId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/reserva/{reservaId}")
    public Mono<ResponseEntity<EntregaView>> obterPorReservaId(@PathVariable String reservaId) {
        return service.obterPorReservaId(reservaId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/motorista/{motoristaId}")
    public Flux<EntregaView> listarPorMotorista(@PathVariable String motoristaId) {
        return service.listarPorMotorista(motoristaId);
    }

    @GetMapping("/status/{status}")
    public Flux<EntregaView> listarPorStatus(@PathVariable String status) {
        return service.listarPorStatus(status);
    }
}
