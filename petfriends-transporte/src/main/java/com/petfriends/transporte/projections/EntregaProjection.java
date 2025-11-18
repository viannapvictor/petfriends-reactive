package com.petfriends.transporte.projections;

import com.petfriends.transporte.events.*;
import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.readmodel.EntregaViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EntregaProjection {
    
    private final EntregaViewRepository viewRepository;
    
    @Bean
    public Function<Flux<BaseEvent<?>>, Mono<Void>> transporteEventsIn() {
        return flux -> flux
            .flatMap(this::handleEvent)
            .doOnError(error -> log.error("Error processing event in projection", error))
            .then();
    }
    
    @Bean
    public Function<Flux<Object>, Mono<Void>> almoxarifadoEventsIn() {
        return flux -> flux
            .doOnNext(event -> log.info("Received event from Almoxarifado: {}", 
                event.getClass().getSimpleName()))
            .flatMap(this::handleAlmoxarifadoEvent)
            .then();
    }
    
    private Mono<Void> handleEvent(BaseEvent<?> event) {
        log.info("Processing event in projection: type={}", event.getClass().getSimpleName());
        
        if (event instanceof EntregaAgendada) {
            return handleEntregaAgendada((EntregaAgendada) event);
        } else if (event instanceof TransporteIniciado) {
            return handleTransporteIniciado((TransporteIniciado) event);
        } else if (event instanceof EntregaConcluida) {
            return handleEntregaConcluida((EntregaConcluida) event);
        }
        
        return Mono.empty();
    }
    
    private Mono<Void> handleAlmoxarifadoEvent(Object event) {
        log.debug("Processing Almoxarifado event: {}", event.getClass().getSimpleName());
        return Mono.empty();
    }
    
    private Mono<Void> handleEntregaAgendada(EntregaAgendada event) {
        EntregaView view = EntregaView.builder()
            .id(event.id)
            .pedidoId(event.pedidoId)
            .reservaId(event.reservaId)
            .status("AGENDADA")
            .enderecoCompleto(event.enderecoCompleto)
            .dataPrevisaoEntrega(event.dataPrevisao.toString())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        return viewRepository.save(view)
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EntregaAgendada - entregaId={}", event.id));
    }
    
    private Mono<Void> handleTransporteIniciado(TransporteIniciado event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("EM_TRANSITO");
                view.setMotoristaId(event.motoristaId);
                view.setVeiculoId(event.veiculoId);
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: TransporteIniciado - entregaId={}", event.id));
    }
    
    private Mono<Void> handleEntregaConcluida(EntregaConcluida event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("CONCLUIDA");
                view.setRecebedor(event.recebedor);
                view.setDataHoraRecebimento(event.dataHoraRecebimento.toString());
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EntregaConcluida - entregaId={}", event.id));
    }
}

