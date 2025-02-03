package br.com.fiap.techchallenge.infra.repository;

import br.com.fiap.techchallenge.infra.repository.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
