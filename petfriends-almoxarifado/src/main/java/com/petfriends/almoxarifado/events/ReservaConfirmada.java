package com.petfriends.almoxarifado.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservaConfirmada extends BaseEvent<String> {

    public final String pedidoId;

    public ReservaConfirmada() {
        super();
        this.pedidoId = null;
    }

    @JsonCreator
    public ReservaConfirmada(
            @JsonProperty("id") String id,
            @JsonProperty("pedidoId") String pedidoId) {
        super(id);
        this.pedidoId = pedidoId;
    }
}