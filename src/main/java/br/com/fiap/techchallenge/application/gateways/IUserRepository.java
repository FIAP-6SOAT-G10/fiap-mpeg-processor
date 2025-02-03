package br.com.fiap.techchallenge.application.gateways;

import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;

public interface IUserRepository {
    UserDTO saveUser(UserDTO user);
}
