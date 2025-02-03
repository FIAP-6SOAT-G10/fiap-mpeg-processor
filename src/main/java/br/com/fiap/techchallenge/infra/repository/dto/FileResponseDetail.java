package br.com.fiap.techchallenge.infra.repository.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDetail {

    private String filename;
    private String type;

}
