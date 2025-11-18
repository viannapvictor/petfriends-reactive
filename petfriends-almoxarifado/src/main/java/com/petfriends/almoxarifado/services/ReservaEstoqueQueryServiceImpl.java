package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service de Queries - CQRS Query Side
 * Usa Read Model otimizado para consultas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaEstoqueQueryServiceImpl implements ReservaEstoqueQueryService {

    private final ReservaEstoqueViewRepository viewRepository;

    @Override
    public Mono<ReservaEstoqueView> obterPorId(String id) {
        return viewRepository.findById(id)
            .doOnSuccess(view -> log.debug("Reserva encontrada: id={}", id))
            .doOnError(error -> log.error("Erro ao buscar reserva: id={}", id, error));
    }

    @Override
    public Mono<ReservaEstoqueView> obterPorPedidoId(String pedidoId) {
        return viewRepository.findByPedidoId(pedidoId)
            .doOnSuccess(view -> log.debug("Reserva encontrada por pedidoId: {}", pedidoId))
            .doOnError(error -> log.error("Erro ao buscar reserva por pedidoId: {}", pedidoId, error));
    }
}
