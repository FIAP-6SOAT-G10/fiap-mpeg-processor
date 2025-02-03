package br.com.fiap.techchallenge.domain.enums;

public enum StatusEnum {

    SUCESS("SUCESS"),
    ERROR("ERROR");

    private final String nominalStatus;

    private StatusEnum(final String nominalStatus) {
        this.nominalStatus = nominalStatus;
    }

    public String getNominalStatus() {
        return nominalStatus;
    }
}
