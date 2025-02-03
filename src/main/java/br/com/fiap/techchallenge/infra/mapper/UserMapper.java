package br.com.fiap.techchallenge.infra.mapper;

import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.entity.User;

public class UserMapper {

    public User fromDomainToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setBucket(userDTO.getBucket());
        user.setData(userDTO.getData());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setProtocol(userDTO.getProtocol());
        user.setStatus(userDTO.getStatus());
        return user;
    }

    public UserDTO fromEntityToDomain(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setBucket(user.getBucket());
        dto.setData(user.getData());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProtocol(user.getProtocol());
        dto.setStatus(user.getStatus());
        return dto;
    }


}
