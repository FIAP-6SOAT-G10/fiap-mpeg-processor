package br.com.fiap.techchallenge.infra.gateways;

import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.infra.mapper.UserMapper;
import br.com.fiap.techchallenge.infra.repository.UserRepository;
import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepositoryGateway implements IUserRepository {

    private final UserMapper userMapper;
    private final UserRepository repository;

    public UserRepositoryGateway(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        return userMapper.fromEntityToDomain(repository.save(userMapper.fromDomainToEntity(userDTO)));
    }
}
