package br.com.fiap.techchallenge.infra.entrypoints.queue;

import br.com.fiap.techchallenge.application.usecases.ProcessorUseCase;
import br.com.fiap.techchallenge.infra.entrypoints.queue.model.ProcessorRequestDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessorQueueListener {

    private final ProcessorUseCase useCase;

    @SqsListener("${aws.sqs.processor.queue}")
    public void listen(ProcessorRequestDTO request) throws IOException {
        log.info("Mensagem recebida da fila {}", request);
        useCase.execute(request);
        log.info("Mensagem processada com sucesso");
    }

}
