package com.petfriends.transporte.events.almoxarifado;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petfriends.transporte.domain.Endereco;

import java.time.LocalDateTime;

public class ItensSeparados {

    public final String id;
    public final String pedidoId;
    public final Endereco enderecoEntrega;
    public final String operadorId;
    public final LocalDateTime dataSeparacao;

    public ItensSeparados() {
        this.id = null;
        this.pedidoId = null;
        this.enderecoEntrega = null;
        this.operadorId = null;
        this.dataSeparacao = null;
    }

    @JsonCreator
    public ItensSeparados(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("enderecoEntrega") Endereco enderecoEntrega,
            @JsonProperty("operadorId") String operadorId,
            @JsonProperty("dataSeparacao") LocalDateTime dataSeparacao) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.enderecoEntrega = enderecoEntrega;
        this.operadorId = operadorId;
        this.dataSeparacao = dataSeparacao != null ? dataSeparacao : LocalDateTime.now();
    }
}

