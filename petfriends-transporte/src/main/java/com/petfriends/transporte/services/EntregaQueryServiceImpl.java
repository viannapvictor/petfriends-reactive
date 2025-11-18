package com.petfriends.transporte.services;

import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.readmodel.EntregaViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntregaQueryServiceImpl implements EntregaQueryService {

    private final EntregaViewRepository viewRepository;

    @Override
    public Mono<EntregaView> obterPorId(String id) {
        return viewRepository.findById(id)
            .doOnSuccess(view -> log.debug("Entrega encontrada: id={}", id))
            .doOnError(error -> log.error("Erro ao buscar entrega: id={}", id, error));
    }

    @Override
    public Mono<EntregaView> obterPorPedidoId(String pedidoId) {
        return viewRepository.findByPedidoId(pedidoId)
            .doOnSuccess(view -> log.debug("Entrega encontrada por pedidoId: {}", pedidoId))
            .doOnError(error -> log.error("Erro ao buscar entrega por pedidoId: {}", pedidoId, error));
    }

    @Override
    public Mono<EntregaView> obterPorReservaId(String reservaId) {
        return viewRepository.findByReservaId(reservaId)
            .doOnSuccess(view -> log.debug("Entrega encontrada por reservaId: {}", reservaId))
            .doOnError(error -> log.error("Erro ao buscar entrega por reservaId: {}", reservaId, error));
    }

    @Override
    public Flux<EntregaView> listarPorMotorista(String motoristaId) {
        return viewRepository.findByMotoristaId(motoristaId)
            .doOnComplete(() -> log.debug("Entregas listadas para motorista: {}", motoristaId))
            .doOnError(error -> log.error("Erro ao listar entregas do motorista: {}", motoristaId, error));
    }

    @Override
    public Flux<EntregaView> listarPorStatus(String status) {
        return viewRepository.findByStatus(status)
            .doOnComplete(() -> log.debug("Entregas listadas por status: {}", status))
            .doOnError(error -> log.error("Erro ao listar entregas por status: {}", status, error));
    }
}
