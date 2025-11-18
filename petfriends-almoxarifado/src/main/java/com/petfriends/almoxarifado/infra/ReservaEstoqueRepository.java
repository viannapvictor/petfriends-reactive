package com.petfriends.almoxarifado.infra;

import com.petfriends.almoxarifado.domain.ReservaEstoque;
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
public interface ReservaEstoqueRepository extends ReactiveCrudRepository<ReservaEstoque, String> {
    Mono<ReservaEstoque> findByPedidoId(String pedidoId);
}