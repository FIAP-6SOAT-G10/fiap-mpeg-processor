package br.com.fiap.techchallenge.infra.repository.dto;

import java.io.Serializable;

public class UserDetailDTO implements Serializable {

    private String id;
    private String filename;
    private String status;
    private String error;
    private UserDTO user;

    public UserDetailDTO(String id, String filename, String status, UserDTO user, String error) {
        this.id = id;
        this.filename = filename;
        this.status = status;
        this.user = user;
        this.error = error;
    }

    public UserDetailDTO(String filename, String status, UserDTO user, String error) {
        this.filename = filename;
        this.status = status;
        this.user = user;
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
