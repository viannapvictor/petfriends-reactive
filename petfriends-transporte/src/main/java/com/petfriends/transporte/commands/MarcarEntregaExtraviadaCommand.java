package com.petfriends.transporte.commands;

import java.time.LocalDateTime;

public class MarcarEntregaExtraviadaCommand extends BaseCommand<String> {

    public final String motivo;
    public final LocalDateTime dataExtravio;
    public final String localUltimoRegistro;

    public MarcarEntregaExtraviadaCommand(String id, String motivo,
                                          LocalDateTime dataExtravio, String localUltimoRegistro) {
        super(id);
        this.motivo = motivo;
        this.dataExtravio = dataExtravio;
        this.localUltimoRegistro = localUltimoRegistro;
    }
}