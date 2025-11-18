package com.petfriends.transporte.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;

    public Mono<Void> publish(BaseEvent<?> event) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("Publicando evento: type={}, aggregateId={}", 
                    event.getClass().getSimpleName(), event.getAggregateId());
                
                boolean sent = streamBridge.send("output-events", event);
                
                if (sent) {
                    log.info("Evento publicado com sucesso: type={}", event.getClass().getSimpleName());
                } else {
                    log.error("Falha ao publicar evento: type={}", event.getClass().getSimpleName());
                    throw new RuntimeException("Falha ao publicar evento no Kafka");
                }
            } catch (Exception e) {
                log.error("Erro ao publicar evento: {}", e.getMessage(), e);
                throw new RuntimeException("Erro ao publicar evento", e);
            }
        });
    }
}

