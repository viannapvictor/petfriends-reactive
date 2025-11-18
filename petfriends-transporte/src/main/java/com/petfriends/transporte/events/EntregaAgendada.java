package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class EntregaAgendada extends BaseEvent<String> {

    public final String pedidoId;
    public final String reservaId;
    public final String enderecoCompleto;
    public final LocalDate dataPrevisao;

    public EntregaAgendada() {
        super();
        this.pedidoId = null;
        this.reservaId = null;
        this.enderecoCompleto = null;
        this.dataPrevisao = null;
    }

    @JsonCreator
    public EntregaAgendada(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("reservaId") String reservaId,
            @JsonProperty("enderecoCompleto") String enderecoCompleto,
            @JsonProperty("dataPrevisao") LocalDate dataPrevisao) {
        super(id);
        this.pedidoId = pedidoId;
        this.reservaId = reservaId;
        this.enderecoCompleto = enderecoCompleto;
        this.dataPrevisao = dataPrevisao;
    }
}