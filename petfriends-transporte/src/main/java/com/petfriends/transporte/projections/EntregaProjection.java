package com.petfriends.transporte.projections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.transporte.commands.AgendarEntregaCommand;
import com.petfriends.transporte.events.*;
import com.petfriends.transporte.events.almoxarifado.ItensSeparados;
import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.readmodel.EntregaViewRepository;
import com.petfriends.transporte.services.EntregaCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EntregaProjection {
    
    private final EntregaViewRepository viewRepository;
    private final EntregaCommandService commandService;
    private final ObjectMapper objectMapper;
    
    @Bean
    public Function<Flux<BaseEvent<?>>, Mono<Void>> transporteEventsIn() {
        return flux -> flux
            .doOnNext(event -> log.info(">>> RECEIVED EVENT: type={}, id={}", event.getClass().getSimpleName(), event.getAggregateId()))
            .flatMap(this::handleEvent)
            .doOnError(error -> log.error("Error processing event in projection", error))
            .then();
    }
    
    @Bean
    public Function<Flux<String>, Mono<Void>> almoxarifadoEventsIn() {
        return flux -> flux
            .flatMap(this::deserializeAndHandleAlmoxarifadoEvent)
            .doOnError(error -> log.error("Error in almoxarifadoEventsIn", error))
            .onErrorResume(error -> {
                log.error("Recovered from error in Kafka consumer", error);
                return Mono.empty();
            })
            .then();
    }
    
    private Mono<Void> handleEvent(BaseEvent<?> event) {
        if (event instanceof EntregaAgendada) {
            return handleEntregaAgendada((EntregaAgendada) event);
        } else if (event instanceof TransporteIniciado) {
            return handleTransporteIniciado((TransporteIniciado) event);
        } else if (event instanceof EntregaConcluida) {
            return handleEntregaConcluida((EntregaConcluida) event);
        } else if (event instanceof EntregaDevolvida) {
            return handleEntregaDevolvida((EntregaDevolvida) event);
        } else if (event instanceof EntregaExtraviada) {
            return handleEntregaExtraviada((EntregaExtraviada) event);
        }

        return Mono.empty();
    }
    
    private Mono<Void> deserializeAndHandleAlmoxarifadoEvent(String message) {
        return Mono.fromCallable(() -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(message);

                if (jsonNode.has("operadorId") && jsonNode.has("dataSeparacao")) {
                    ItensSeparados event = objectMapper.treeToValue(jsonNode, ItensSeparados.class);
                    return event;
                }

                return null;

            } catch (Exception e) {
                return null;
            }
        })
        .flatMap(event -> {
            if (event != null && event instanceof ItensSeparados) {
                return handleItensSeparados((ItensSeparados) event);
            }
            return Mono.empty();
        })
        .doOnError(error -> log.error("Error in deserializeAndHandleAlmoxarifadoEvent", error));
    }
    
    private Mono<Void> handleItensSeparados(ItensSeparados event) {
        if (event.enderecoEntrega == null) {
            return Mono.empty();
        }

        AgendarEntregaCommand.EnderecoDTO enderecoDTO = new AgendarEntregaCommand.EnderecoDTO(
            event.enderecoEntrega.getRua(),
            event.enderecoEntrega.getNumero(),
            event.enderecoEntrega.getComplemento(),
            event.enderecoEntrega.getBairro(),
            event.enderecoEntrega.getCidade(),
            event.enderecoEntrega.getEstado(),
            event.enderecoEntrega.getCep()
        );

        String dataPrevisao = LocalDate.now().plusDays(3).toString();

        return commandService.agendarEntrega(event.pedidoId, event.id, enderecoDTO, dataPrevisao)
            .doOnError(error -> log.error("FAILED to create automatic delivery for reservaId={}", event.id, error))
            .then()
            .onErrorResume(error -> Mono.empty());
    }
    
    private Mono<Void> handleEntregaAgendada(EntregaAgendada event) {
        log.info(">>> HANDLING EntregaAgendada: id={}, pedidoId={}", event.id, event.pedidoId);

        EntregaView view = EntregaView.builder()
            .id(event.id)
            .isNew(true)
            .pedidoId(event.pedidoId)
            .reservaId(event.reservaId)
            .status("AGENDADA")
            .enderecoCompleto(event.enderecoCompleto)
            .dataPrevisaoEntrega(event.dataPrevisao.toString())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return viewRepository.save(view)
            .doOnSuccess(saved -> log.info(">>> SAVED TO entrega_view: id={}", saved.getId()))
            .doOnError(error -> log.error(">>> ERROR SAVING to entrega_view", error))
            .then();
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
            .then();
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

    private Mono<Void> handleEntregaDevolvida(EntregaDevolvida event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("DEVOLVIDA");
                view.setMotivoDevolucao(event.motivo);
                view.setDataDevolucao(event.dataDevolucao.toString());
                view.setResponsavelDevolucao(event.responsavel);
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EntregaDevolvida - entregaId={}", event.id));
    }

    private Mono<Void> handleEntregaExtraviada(EntregaExtraviada event) {
        return viewRepository.findById(event.id)
            .flatMap(view -> {
                view.setStatus("EXTRAVIADA");
                view.setMotivoExtravio(event.motivo);
                view.setDataExtravio(event.dataExtravio.toString());
                view.setLocalUltimoRegistro(event.localUltimoRegistro);
                view.setUpdatedAt(LocalDateTime.now());
                view.markAsExisting();
                return viewRepository.save(view);
            })
            .then()
            .doOnSuccess(v -> log.info("Read model updated: EntregaExtraviada - entregaId={}", event.id));
    }
}

