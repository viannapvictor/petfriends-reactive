package com.petfriends.transporte.infra;

import com.petfriends.transporte.domain.Entrega;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository Reativo (R2DBC) - NÃO USADO em Event Sourcing
 * 
 * NOTA: Em Event Sourcing, o estado é reconstituído a partir dos eventos.
 * Este repository existe apenas para casos específicos onde precisamos
 * persistir snapshots ou views do agregado, mas NÃO é a fonte da verdade.
 */
@Repository
public interface EntregaRepository extends ReactiveCrudRepository<Entrega, String> {

    Mono<Entrega> findByPedidoId(String pedidoId);
    Mono<Entrega> findByReservaId(String reservaId);
}