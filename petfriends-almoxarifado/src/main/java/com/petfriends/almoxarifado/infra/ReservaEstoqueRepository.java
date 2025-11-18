package com.petfriends.almoxarifado.infra;

import com.petfriends.almoxarifado.domain.ReservaEstoque;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReservaEstoqueRepository extends ReactiveCrudRepository<ReservaEstoque, String> {
    Mono<ReservaEstoque> findByPedidoId(String pedidoId);
}