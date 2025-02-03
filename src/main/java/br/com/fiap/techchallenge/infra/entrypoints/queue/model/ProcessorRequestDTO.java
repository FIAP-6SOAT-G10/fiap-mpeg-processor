package br.com.fiap.techchallenge.infra.entrypoints.queue.model;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessorRequestDTO {

    private String protocol;
    private UserRequestDTO user;
}