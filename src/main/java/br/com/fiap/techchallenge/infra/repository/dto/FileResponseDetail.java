package br.com.fiap.techchallenge.infra.repository.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDetail {

    private String filename;
    private String status;

   @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

}
