package com.petfriends.almoxarifado.controllers;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.services.ReservaEstoqueQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/almoxarifado/reservas")
@RequiredArgsConstructor
public class ReservaEstoqueQueryController {

    private final ReservaEstoqueQueryService service;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ReservaEstoqueView>> obterPorId(@PathVariable String id) {
        return service.obterPorId(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public Mono<ResponseEntity<ReservaEstoqueView>> obterPorPedidoId(@PathVariable String pedidoId) {
        return service.obterPorPedidoId(pedidoId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
