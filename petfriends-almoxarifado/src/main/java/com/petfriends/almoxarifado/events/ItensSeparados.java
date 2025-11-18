package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ItensSeparados extends BaseEvent<String> {

    public final String pedidoId;
    public final String operadorId;
    public final LocalDateTime dataSeparacao;

    public ItensSeparados() {
        super();
        this.pedidoId = null;
        this.operadorId = null;
        this.dataSeparacao = null;
    }

    @JsonCreator
    public ItensSeparados(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("operadorId") String operadorId,
            @JsonProperty("dataSeparacao") LocalDateTime dataSeparacao) {
        super(id);
        this.pedidoId = pedidoId;
        this.operadorId = operadorId;
        this.dataSeparacao = dataSeparacao != null ? dataSeparacao : LocalDateTime.now();
    }

    public ItensSeparados(String id, String pedidoId, String operadorId) {
        super(id);
        this.pedidoId = pedidoId;
        this.operadorId = operadorId;
        this.dataSeparacao = LocalDateTime.now();
    }
}