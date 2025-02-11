package br.com.fiap.techchallenge.infra.mapper;

import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDetailDTO;
import br.com.fiap.techchallenge.infra.repository.entity.User;
import br.com.fiap.techchallenge.infra.repository.entity.UserDetail;

import java.util.ArrayList;
import java.util.List;

public class UserDetailMapper {

    public UserDetail fromDomainToEntity(UserDetailDTO userDetailDTO) {

        UserDetail detail = new UserDetail();
        detail.setId(userDetailDTO.getId());
        detail.setStatus(userDetailDTO.getStatus());
        detail.setFilename(userDetailDTO.getFilename());
        detail.setError(userDetailDTO.getError());

        User user = new User();
        user.setId(userDetailDTO.getUser().getId());
        user.setBucket(userDetailDTO.getUser().getBucket());
        user.setData(userDetailDTO.getUser().getData());
        user.setName(userDetailDTO.getUser().getName());
        user.setEmail(userDetailDTO.getUser().getEmail());
        user.setProtocol(userDetailDTO.getUser().getProtocol());
        detail.setUser(user);

        return detail;
    }

    public UserDetailDTO fromEntityToDomain(UserDetail detail) {
        UserDTO user = new UserDTO();
        user.setId(detail.getUser().getId());
        user.setBucket(detail.getUser().getBucket());
        user.setData(detail.getUser().getData());
        user.setName(detail.getUser().getName());
        user.setEmail(detail.getUser().getEmail());
        user.setProtocol(detail.getUser().getProtocol());
        return  new UserDetailDTO(detail.getId(), detail.getFilename(), detail.getStatus(), user, detail.getError() );
    }

    public List<UserDetailDTO> fromListEntityToListDomain(List<UserDetail> listDetail){
        List<UserDetailDTO> userDetailDTOList = new ArrayList<>();
        listDetail.forEach( it -> {
            userDetailDTOList.add(fromEntityToDomain(it));
        });
        return userDetailDTOList;
    }
}
