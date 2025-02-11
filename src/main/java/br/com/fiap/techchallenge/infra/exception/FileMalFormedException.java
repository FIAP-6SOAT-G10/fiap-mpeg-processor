package br.com.fiap.techchallenge.infra.exception;

import br.com.fiap.techchallenge.domain.enums.ErrosEnum;

public class FileMalFormedException extends RuntimeException {

    private final ErrosEnum error;

    public FileMalFormedException(ErrosEnum error) {
        this.error = error;
    }

    public ErrosEnum getError() {
        return error;
    }
}
