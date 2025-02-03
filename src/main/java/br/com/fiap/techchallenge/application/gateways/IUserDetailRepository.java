package br.com.fiap.techchallenge.application.gateways;

import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDetailDTO;
import br.com.fiap.techchallenge.infra.repository.entity.UserDetail;

import java.util.List;

public interface IUserDetailRepository {

    UserDetailDTO saveUserDetail(UserDetailDTO userDetailDTO);

    List<UserDetailDTO> findByUser(UserDTO user);
}
