package com.petfriends.transporte.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TransporteIniciado extends BaseEvent<String> {

    public final String motoristaId;
    public final String veiculoId;
    public final LocalDateTime dataHoraSaida;

    public TransporteIniciado() {
        super();
        this.motoristaId = null;
        this.veiculoId = null;
        this.dataHoraSaida = null;
    }

    @JsonCreator
    public TransporteIniciado(
            @JsonProperty("id") String id,
            @JsonProperty("motoristaId") String motoristaId,
            @JsonProperty("veiculoId") String veiculoId,
            @JsonProperty("dataHoraSaida") LocalDateTime dataHoraSaida) {
        super(id);
        this.motoristaId = motoristaId;
        this.veiculoId = veiculoId;
        this.dataHoraSaida = dataHoraSaida != null ? dataHoraSaida : LocalDateTime.now();
    }

    public TransporteIniciado(String id, String motoristaId, String veiculoId) {
        super(id);
        this.motoristaId = motoristaId;
        this.veiculoId = veiculoId;
        this.dataHoraSaida = LocalDateTime.now();
    }
}