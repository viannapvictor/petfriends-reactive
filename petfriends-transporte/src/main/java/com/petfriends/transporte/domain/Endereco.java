package com.petfriends.transporte.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Endereco {

    private final String rua;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;

    @JsonCreator
    public Endereco(
            @JsonProperty("rua") String rua,
            @JsonProperty("numero") String numero,
            @JsonProperty("complemento") String complemento,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cidade") String cidade,
            @JsonProperty("estado") String estado,
            @JsonProperty("cep") String cep) {
        
        validar(rua, numero, bairro, cidade, estado, cep);
        
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    private void validar(String rua, String numero, String bairro, 
                        String cidade, String estado, String cep) {
        if (rua == null || rua.trim().isEmpty()) {
            throw new IllegalArgumentException("Rua é obrigatória");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido");
        }
    }

    public String formatado() {
        return String.format("%s, %s%s - %s, %s/%s - CEP: %s",
                rua,
                numero,
                complemento != null && !complemento.isEmpty() ? " " + complemento : "",
                bairro,
                cidade,
                estado,
                cep);
    }

    @Override
    public String toString() {
        return formatado();
    }
}

