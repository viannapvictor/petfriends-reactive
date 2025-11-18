package com.petfriends.almoxarifado.services;

import com.petfriends.almoxarifado.commands.*;
import com.petfriends.almoxarifado.domain.ReservaEstoque;
import com.petfriends.almoxarifado.events.*;
import com.petfriends.almoxarifado.eventstore.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service de Comandos - CQRS Command Side
 * Arquitetura Reativa com Event Sourcing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaEstoqueCommandServiceImpl implements ReservaEstoqueCommandService {

    private final EventStoreService eventStoreService;
    private final EventPublisher eventPublisher;

    @Override
    public Mono<String> reservarEstoque(String pedidoId, List<ItemReservaRequest> itens) {
        String reservaId = UUID.randomUUID().toString();

        List<ReservarEstoqueCommand.ItemReservaDTO> itensDTO = itens.stream()
                .map(item -> new ReservarEstoqueCommand.ItemReservaDTO(item.produtoId, item.quantidade))
                .collect(Collectors.toList());

        ReservarEstoqueCommand comando = new ReservarEstoqueCommand(reservaId, pedidoId, itensDTO);
        
        // Criar novo agregado e executar comando
        ReservaEstoque agregado = new ReservaEstoque(reservaId);
        BaseEvent<?> evento = agregado.reservarEstoque(comando);
        
        // Aplicar evento ao agregado (atualizar estado)
        agregado.apply(evento);

        // Persistir evento no Event Store e publicar no Kafka
        return eventStoreService.appendEvent(reservaId, "ReservaEstoque", evento)
            .flatMap(entry -> eventPublisher.publish(evento))
            .thenReturn(reservaId)
            .doOnSuccess(id -> log.info("Estoque reservado: reservaId={}, pedidoId={}", id, pedidoId))
            .doOnError(error -> log.error("Erro ao reservar estoque: pedidoId={}", pedidoId, error));
    }

    @Override
    public Mono<String> confirmarReserva(String id) {
        return reconstituirAgregado(id)
            .flatMap(agregado -> {
                ConfirmarReservaCommand comando = new ConfirmarReservaCommand(id);
                BaseEvent<?> evento = agregado.confirmarReserva(comando);
                agregado.apply(evento);
                
                return eventStoreService.appendEvent(id, "ReservaEstoque", evento)
                    .flatMap(entry -> eventPublisher.publish(evento))
                    .thenReturn(id);
            })
            .doOnSuccess(reservaId -> log.info("Reserva confirmada: reservaId={}", reservaId))
            .doOnError(error -> log.error("Erro ao confirmar reserva: id={}", id, error));
    }

    @Override
    public Mono<String> cancelarReserva(String id, String motivo) {
        return reconstituirAgregado(id)
            .flatMap(agregado -> {
                CancelarReservaCommand comando = new CancelarReservaCommand(id, motivo);
                BaseEvent<?> evento = agregado.cancelarReserva(comando);
                agregado.apply(evento);
                
                return eventStoreService.appendEvent(id, "ReservaEstoque", evento)
                    .flatMap(entry -> eventPublisher.publish(evento))
                    .thenReturn(id);
            })
            .doOnSuccess(reservaId -> log.info("Reserva cancelada: reservaId={}, motivo={}", reservaId, motivo))
            .doOnError(error -> log.error("Erro ao cancelar reserva: id={}", id, error));
    }

    @Override
    public Mono<String> separarItens(String id, String operadorId) {
        return reconstituirAgregado(id)
            .flatMap(agregado -> {
                SepararItensCommand comando = new SepararItensCommand(id, operadorId);
                BaseEvent<?> evento = agregado.separarItens(comando);
                agregado.apply(evento);
                
                return eventStoreService.appendEvent(id, "ReservaEstoque", evento)
                    .flatMap(entry -> eventPublisher.publish(evento))
                    .thenReturn(id);
            })
            .doOnSuccess(reservaId -> log.info("Itens separados: reservaId={}, operadorId={}", reservaId, operadorId))
            .doOnError(error -> log.error("Erro ao separar itens: id={}", id, error));
    }

    /**
     * Reconstitui o agregado a partir dos eventos no Event Store
     */
    private Mono<ReservaEstoque> reconstituirAgregado(String aggregateId) {
        return eventStoreService.loadEvents(aggregateId)
            .flatMap(entry -> eventStoreService.deserializeEvent(entry, getEventClass(entry.getEventType())))
            .collectList()
            .map(events -> {
                ReservaEstoque agregado = new ReservaEstoque(aggregateId);
                events.forEach(evento -> {
                    log.debug("Replaying event: type={}, version={}", evento.getClass().getSimpleName(), events.indexOf(evento) + 1);
                    agregado.apply((BaseEvent<?>) evento);
                });
                log.info("Aggregate reconstituted: id={}, status={}, events={}", aggregateId, agregado.getStatus(), events.size());
                return agregado;
            });
    }
    
    private Class<?> getEventClass(String eventType) {
        switch (eventType) {
            case "EstoqueReservado":
                return EstoqueReservado.class;
            case "EstoqueInsuficiente":
                return EstoqueInsuficiente.class;
            case "ReservaConfirmada":
                return ReservaConfirmada.class;
            case "ReservaCancelada":
                return ReservaCancelada.class;
            case "ItensSeparados":
                return ItensSeparados.class;
            default:
                throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
}
