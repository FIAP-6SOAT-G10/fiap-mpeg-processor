package br.com.fiap.techchallenge.infra.exception;

import br.com.fiap.techchallenge.domain.enums.ErrosEnum;

public class FormatFileException extends RuntimeException {

    private final ErrosEnum error;

    public FormatFileException(ErrosEnum error) {
        this.error = error;
    }
}
