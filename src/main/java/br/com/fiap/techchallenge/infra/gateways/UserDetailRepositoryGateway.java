package br.com.fiap.techchallenge.infra.gateways;

import br.com.fiap.techchallenge.application.gateways.IUserDetailRepository;
import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.infra.mapper.UserDetailMapper;
import br.com.fiap.techchallenge.infra.mapper.UserMapper;
import br.com.fiap.techchallenge.infra.repository.UserDetailRepository;
import br.com.fiap.techchallenge.infra.repository.UserRepository;
import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDetailDTO;
import br.com.fiap.techchallenge.infra.repository.entity.User;
import br.com.fiap.techchallenge.infra.repository.entity.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserDetailRepositoryGateway implements IUserDetailRepository {

    private final UserDetailMapper userDetailMapper;
    private final UserMapper userMapper;
    private final UserDetailRepository repository;

    public UserDetailRepositoryGateway(UserDetailRepository repository, UserDetailMapper userDetailMapper, UserMapper userMapper) {
        this.repository = repository;
        this.userDetailMapper = userDetailMapper;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetailDTO saveUserDetail(UserDetailDTO userDetailDTO) {
        return userDetailMapper.fromEntityToDomain(repository.save(userDetailMapper.fromDomainToEntity(userDetailDTO)));
    }

    @Override
    public List<UserDetailDTO> findByUser(UserDTO userDTO) {
        User user = userMapper.fromDomainToEntity(userDTO);
        List<UserDetail> details = repository.findByUser(user);
        return userDetailMapper.fromListEntityToListDomain(details);
    }
}
