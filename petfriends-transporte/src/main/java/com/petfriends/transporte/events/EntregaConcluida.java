package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EntregaConcluida extends BaseEvent<String> {

    public final String pedidoId;
    public final String recebedor;
    public final LocalDateTime dataHoraRecebimento;
    public final String observacoes;

    public EntregaConcluida() {
        super();
        this.pedidoId = null;
        this.recebedor = null;
        this.dataHoraRecebimento = null;
        this.observacoes = null;
    }

    @JsonCreator
    public EntregaConcluida(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("recebedor") String recebedor,
            @JsonProperty("dataHoraRecebimento") LocalDateTime dataHoraRecebimento,
            @JsonProperty("observacoes") String observacoes) {
        super(id);
        this.pedidoId = pedidoId;
        this.recebedor = recebedor;
        this.dataHoraRecebimento = dataHoraRecebimento;
        this.observacoes = observacoes;
    }
}