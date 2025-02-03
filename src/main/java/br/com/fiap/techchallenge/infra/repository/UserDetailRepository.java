package br.com.fiap.techchallenge.infra.repository;

import br.com.fiap.techchallenge.infra.repository.entity.User;
import br.com.fiap.techchallenge.infra.repository.entity.UserDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDetailRepository extends MongoRepository<UserDetail, String> {

    List<UserDetail> findByUser(User user);
}
