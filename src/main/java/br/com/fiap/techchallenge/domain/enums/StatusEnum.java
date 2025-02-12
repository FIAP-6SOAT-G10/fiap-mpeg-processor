package br.com.fiap.techchallenge.domain.enums;

public enum StatusEnum {

    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private final String nominalStatus;

    private StatusEnum(final String nominalStatus) {
        this.nominalStatus = nominalStatus;
    }

    public String getNominalStatus() {
        return nominalStatus;
    }
}
