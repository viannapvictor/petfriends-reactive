package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EntregaExtraviada extends BaseEvent<String> {

    public final String pedidoId;
    public final String motivo;
    public final LocalDateTime dataExtravio;
    public final String localUltimoRegistro;

    public EntregaExtraviada() {
        super();
        this.pedidoId = null;
        this.motivo = null;
        this.dataExtravio = null;
        this.localUltimoRegistro = null;
    }

    @JsonCreator
    public EntregaExtraviada(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("motivo") String motivo,
            @JsonProperty("dataExtravio") LocalDateTime dataExtravio,
            @JsonProperty("localUltimoRegistro") String localUltimoRegistro) {
        super(id);
        this.pedidoId = pedidoId;
        this.motivo = motivo;
        this.dataExtravio = dataExtravio;
        this.localUltimoRegistro = localUltimoRegistro;
    }
}