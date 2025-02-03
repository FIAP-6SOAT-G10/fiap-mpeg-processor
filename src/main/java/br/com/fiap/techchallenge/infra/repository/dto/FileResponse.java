package br.com.fiap.techchallenge.infra.repository.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String recipientEmail;
    private String recipientName;
    private String url;
    private UUID protocol;
    private List<FileResponseDetail> files;
}
