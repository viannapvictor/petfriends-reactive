package com.petfriends.transporte.services;

import com.petfriends.transporte.commands.*;
import com.petfriends.transporte.domain.Entrega;
import com.petfriends.transporte.events.*;
import com.petfriends.transporte.eventstore.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service de Comandos - CQRS Command Side
 * Arquitetura Reativa com Event Sourcing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntregaCommandServiceImpl implements EntregaCommandService {

    private final EventStoreService eventStoreService;
    private final EventPublisher eventPublisher;

    @Override
    public Mono<String> agendarEntrega(String pedidoId, String reservaId, 
                                        AgendarEntregaCommand.EnderecoDTO endereco, 
                                        String dataPrevisaoEntrega) {
        String entregaId = UUID.randomUUID().toString();
        
        AgendarEntregaCommand comando = new AgendarEntregaCommand(
            entregaId, pedidoId, reservaId, endereco, LocalDate.parse(dataPrevisaoEntrega)
        );
        
        // Criar novo agregado e executar comando
        Entrega agregado = new Entrega(entregaId);
        BaseEvent<?> evento = agregado.agendarEntrega(comando);
        
        // Aplicar evento ao agregado
        agregado.apply(evento);

        // Persistir evento no Event Store e publicar no Kafka
        return eventStoreService.appendEvent(entregaId, "Entrega", evento)
            .flatMap(entry -> eventPublisher.publish(evento))
            .thenReturn(entregaId)
            .doOnSuccess(id -> log.info("Entrega agendada: entregaId={}, pedidoId={}", id, pedidoId))
            .doOnError(error -> log.error("Erro ao agendar entrega: pedidoId={}", pedidoId, error));
    }

    @Override
    public Mono<String> iniciarTransporte(String id, String motoristaId, String veiculoId) {
        return reconstituirAgregado(id)
            .flatMap(agregado -> {
                IniciarTransporteCommand comando = new IniciarTransporteCommand(id, motoristaId, veiculoId);
                BaseEvent<?> evento = agregado.iniciarTransporte(comando);
                agregado.apply(evento);
                
                return eventStoreService.appendEvent(id, "Entrega", evento)
                    .flatMap(entry -> eventPublisher.publish(evento))
                    .thenReturn(id);
            })
            .doOnSuccess(entregaId -> log.info("Transporte iniciado: entregaId={}, motoristaId={}", entregaId, motoristaId))
            .doOnError(error -> log.error("Erro ao iniciar transporte: id={}", id, error));
    }

    @Override
    public Mono<String> concluirEntrega(String id, String recebedor, 
                                         String dataRecebimento, String observacoes) {
        return reconstituirAgregado(id)
            .flatMap(agregado -> {
                ConcluirEntregaCommand comando = new ConcluirEntregaCommand(
                    id, recebedor, LocalDateTime.parse(dataRecebimento), observacoes
                );
                BaseEvent<?> evento = agregado.concluirEntrega(comando);
                agregado.apply(evento);
                
                return eventStoreService.appendEvent(id, "Entrega", evento)
                    .flatMap(entry -> eventPublisher.publish(evento))
                    .thenReturn(id);
            })
            .doOnSuccess(entregaId -> log.info("Entrega concluída: entregaId={}, recebedor={}", entregaId, recebedor))
            .doOnError(error -> log.error("Erro ao concluir entrega: id={}", id, error));
    }

    /**
     * Reconstitui o agregado a partir dos eventos no Event Store
     */
    private Mono<Entrega> reconstituirAgregado(String aggregateId) {
        return eventStoreService.loadEvents(aggregateId)
            .flatMap(entry -> eventStoreService.deserializeEvent(entry, getEventClass(entry.getEventType())))
            .collectList()
            .map(events -> {
                Entrega agregado = new Entrega(aggregateId);
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
            case "EntregaAgendada":
                return EntregaAgendada.class;
            case "TransporteIniciado":
                return TransporteIniciado.class;
            case "EntregaConcluida":
                return EntregaConcluida.class;
            default:
                throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
}
