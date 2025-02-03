package br.com.fiap.techchallenge.application.gateways;

import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;

import java.io.IOException;

public interface IProcessor {
    void processor(UserDTO user) throws IOException;
}
