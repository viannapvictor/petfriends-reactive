package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservaCancelada extends BaseEvent<String> {

    public final String pedidoId;
    public final String motivo;

    public ReservaCancelada() {
        super();
        this.pedidoId = null;
        this.motivo = null;
    }

    @JsonCreator
    public ReservaCancelada(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId,
            @JsonProperty("motivo") String motivo) {
        super(id);
        this.pedidoId = pedidoId;
        this.motivo = motivo;
    }
}