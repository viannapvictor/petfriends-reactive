package com.petfriends.transporte.infra;

import com.petfriends.transporte.domain.Entrega;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EntregaRepository extends ReactiveCrudRepository<Entrega, String> {

    Mono<Entrega> findByPedidoId(String pedidoId);
    Mono<Entrega> findByReservaId(String reservaId);
}