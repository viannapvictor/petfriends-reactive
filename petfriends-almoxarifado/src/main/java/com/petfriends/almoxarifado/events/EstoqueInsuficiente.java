package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EstoqueInsuficiente extends BaseEvent<String> {

    public final String pedidoId;
    public final List<String> produtosIndisponiveis;
    public final String mensagem;

    public EstoqueInsuficiente() {
        super();
        this.pedidoId = null;
        this.produtosIndisponiveis = null;
        this.mensagem = null;
    }

    @JsonCreator
    public EstoqueInsuficiente(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("produtosIndisponiveis") List<String> produtosIndisponiveis,
            @JsonProperty("mensagem") String mensagem) {
        super(id);
        this.pedidoId = pedidoId;
        this.produtosIndisponiveis = produtosIndisponiveis;
        this.mensagem = mensagem;
    }
}