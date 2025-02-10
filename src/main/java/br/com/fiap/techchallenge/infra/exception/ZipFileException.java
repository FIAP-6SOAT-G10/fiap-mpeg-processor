package br.com.fiap.techchallenge.infra.exception;

import br.com.fiap.techchallenge.domain.enums.ErrosEnum;

public class ZipFileException extends RuntimeException {

    private final ErrosEnum error;

    public ZipFileException(ErrosEnum error) {
        this.error = error;
    }

    public ErrosEnum getError() {
        return error;
    }
}
