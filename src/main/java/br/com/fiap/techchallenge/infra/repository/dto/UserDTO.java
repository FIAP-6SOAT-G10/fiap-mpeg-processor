package br.com.fiap.techchallenge.infra.repository.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO implements Serializable {

    private String id;
    private String name;
    private UUID protocol;
    private String email;
    private String bucket;
    private String filename;
    private LocalDateTime data;

    public UserDTO(String name,  String email, UUID protocol, LocalDateTime data) {
        this.name = name;
        this.email = email;
        this.protocol = protocol;
        this.data = data;
    }

    public UserDTO(String id, String name, UUID protocol, String email, String bucket, String status, LocalDateTime data) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.email = email;
        this.bucket = bucket;
        this.data = data;
    }

    public UserDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getProtocol() {
        return protocol;
    }

    public void setProtocol(UUID protocol) {
        this.protocol = protocol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
