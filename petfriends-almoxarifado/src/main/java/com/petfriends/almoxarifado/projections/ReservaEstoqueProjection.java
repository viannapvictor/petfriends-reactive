package com.petfriends.almoxarifado.projections;

import com.petfriends.almoxarifado.events.*;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueItemView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueItemViewRepository;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueView;
import com.petfriends.almoxarifado.readmodel.ReservaEstoqueViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReservaEstoqueProjection {
    
    private final ReservaEstoqueViewRepository viewRepository;
    private final ReservaEstoqueItemViewRepository itemViewRepository;
    
    @Bean
    public Function<Flux<BaseEvent<?>>, Mono<Void>> almoxarifadoEventsIn() {
        return flux -> flux
            .flatMap(this::handleEvent)
            .doOnError(error -> log.error("Error processing event in projection", error))
            .then();
    }
    
    private Mono<Void> handleEvent(BaseEvent<?> event) {
        log.info("Processing event in projection: type={}", event.getClass().getSimpleName());
        
        if (event instanceof EstoqueReservado) {
            return handleEstoqueReservado((EstoqueReservado) event);
        } else if (event instanceof EstoqueInsuficiente) {
            return handleEstoqueInsuficiente((EstoqueInsuficiente) event);
        } else if (event instanceof ReservaConfirmada) {
            return handleReservaConfirmada((ReservaConfirmada) event);
        } else if (event instanceof ReservaCancelada) {
            return handleReservaCancelada((ReservaCancelada) event);
        } else if (event instanceof ItensSeparados) {
            return handleItensSeparados((ItensSeparados) event);
        }
        
        return Mono.empty();
    }
    
    private Mono<Void> handleEstoqueReservado(EstoqueReservado event) {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
            .id(event.id)
            .pedidoId(event.pedidoId)
            .status("PENDENTE")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        return viewRepository.save(view)
            .flatMapMany(savedView -> 
                Flux.fromIterable(event.itens)
                    .map(item -> ReservaEstoqueItemView.builder()
                        .id(UUID.randomUUID())
                        .reservaId(savedView.getId())
                        .produtoId(item.produtoId)
                        .quantidade(item.quantidade)
                        .build())
                    .flatMap(itemViewRepository::save)
            )
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EstoqueReservado - reservaId={}", event.id));
    }
    
    private Mono<Void> handleEstoqueInsuficiente(EstoqueInsuficiente event) {
        ReservaEstoqueView view = ReservaEstoqueView.builder()
            .id(event.id)
            .pedidoId(event.pedidoId)
            .status("INSUFICIENTE")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        return viewRepository.save(view)
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EstoqueInsuficiente - reservaId={}", event.id));
    }
    
    private Mono<Void> handleReservaConfirmada(ReservaConfirmada event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("CONFIRMADA");
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: ReservaConfirmada - reservaId={}", event.id));
    }
    
    private Mono<Void> handleReservaCancelada(ReservaCancelada event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("CANCELADA");
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: ReservaCancelada - reservaId={}", event.id));
    }
    
    private Mono<Void> handleItensSeparados(ItensSeparados event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("SEPARADA");
                view.setOperadorId(event.operadorId);
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: ItensSeparados - reservaId={}", event.id));
    }
}

