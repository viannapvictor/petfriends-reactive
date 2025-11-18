package com.petfriends.transporte.clients;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlmoxarifadoClient {
    
    private final WebClient almoxarifadoWebClient;
    
    public Mono<ReservaEstoqueDTO> consultarReserva(String reservaId) {
        return almoxarifadoWebClient
            .get()
            .uri("/almoxarifado/reservas/{id}", reservaId)
            .retrieve()
            .bodyToMono(ReservaEstoqueDTO.class)
            .doOnSuccess(reserva -> log.info("Reserva consultada: id={}, status={}", 
                reservaId, reserva.getStatus()))
            .doOnError(error -> log.error("Erro ao consultar reserva: id={}", reservaId, error))
            .onErrorResume(error -> {
                log.warn("Reserva não encontrada ou erro na comunicação: id={}", reservaId);
                return Mono.empty();
            });
    }
    
    public Mono<ReservaEstoqueDTO> consultarReservaPorPedido(String pedidoId) {
        return almoxarifadoWebClient
            .get()
            .uri("/almoxarifado/reservas/pedido/{pedidoId}", pedidoId)
            .retrieve()
            .bodyToMono(ReservaEstoqueDTO.class)
            .doOnSuccess(reserva -> log.info("Reserva consultada por pedido: pedidoId={}, status={}", 
                pedidoId, reserva.getStatus()))
            .doOnError(error -> log.error("Erro ao consultar reserva por pedido: pedidoId={}", pedidoId, error))
            .onErrorResume(error -> Mono.empty());
    }
    
    public Mono<Boolean> reservaProntaParaEntrega(String reservaId) {
        return consultarReserva(reservaId)
            .map(reserva -> "SEPARADA".equals(reserva.getStatus()))
            .defaultIfEmpty(false);
    }
    
    @Data
    public static class ReservaEstoqueDTO {
        private String id;
        private String pedidoId;
        private String status;
        private String operadorId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

