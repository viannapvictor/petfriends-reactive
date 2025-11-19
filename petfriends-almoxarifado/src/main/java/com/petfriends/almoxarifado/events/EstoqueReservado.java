package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petfriends.almoxarifado.domain.Endereco;

import java.time.LocalDateTime;
import java.util.List;

public class EstoqueReservado extends BaseEvent<String> {

    public final String pedidoId;
    public final Endereco enderecoEntrega;
    public final List<ItemReservado> itens;
    public final LocalDateTime dataReserva;

    public EstoqueReservado() {
        super();
        this.pedidoId = null;
        this.enderecoEntrega = null;
        this.itens = null;
        this.dataReserva = null;
    }

    @JsonCreator
    public EstoqueReservado(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("enderecoEntrega") Endereco enderecoEntrega,
            @JsonProperty("itens") List<ItemReservado> itens,
            @JsonProperty("dataReserva") LocalDateTime dataReserva) {
        super(id);
        this.pedidoId = pedidoId;
        this.enderecoEntrega = enderecoEntrega;
        this.itens = itens;
        this.dataReserva = dataReserva != null ? dataReserva : LocalDateTime.now();
    }

    public EstoqueReservado(String id, String pedidoId, Endereco enderecoEntrega, List<ItemReservado> itens) {
        super(id);
        this.pedidoId = pedidoId;
        this.enderecoEntrega = enderecoEntrega;
        this.itens = itens;
        this.dataReserva = LocalDateTime.now();
    }

    public static class ItemReservado {
        public final String produtoId;
        public final int quantidade;

        public ItemReservado() {
            this.produtoId = null;
            this.quantidade = 0;
        }

        @JsonCreator
        public ItemReservado(
                @JsonProperty("produtoId") String produtoId,
                @JsonProperty("quantidade") int quantidade) {
            this.produtoId = produtoId;
            this.quantidade = quantidade;
        }
    }
}