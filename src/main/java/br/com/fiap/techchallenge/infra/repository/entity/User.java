package br.com.fiap.techchallenge.infra.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private UUID protocol;
    private String email;
    private String username;
    private String bucket;
    private String status;
    private String error;
    private LocalDateTime data;

}
