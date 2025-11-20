package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EntregaDevolvida extends BaseEvent<String> {

    public final String pedidoId;
    public final String motivo;
    public final LocalDateTime dataDevolucao;
    public final String responsavel;

    public EntregaDevolvida() {
        super();
        this.pedidoId = null;
        this.motivo = null;
        this.dataDevolucao = null;
        this.responsavel = null;
    }

    @JsonCreator
    public EntregaDevolvida(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("motivo") String motivo,
            @JsonProperty("dataDevolucao") LocalDateTime dataDevolucao,
            @JsonProperty("responsavel") String responsavel) {
        super(id);
        this.pedidoId = pedidoId;
        this.motivo = motivo;
        this.dataDevolucao = dataDevolucao;
        this.responsavel = responsavel;
    }
}