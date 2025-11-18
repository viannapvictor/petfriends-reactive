package com.petfriends.almoxarifado.commands;

public class SepararItensCommand extends BaseCommand<String> {

    public final String operadorId;

    public SepararItensCommand(String id, String operadorId) {
        super(id);
        this.operadorId = operadorId;
    }
}