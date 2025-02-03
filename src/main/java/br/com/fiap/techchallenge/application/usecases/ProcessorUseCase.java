package br.com.fiap.techchallenge.application.usecases;

import br.com.fiap.techchallenge.application.gateways.IProcessor;
import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.infra.entrypoints.queue.model.ProcessorRequestDTO;
import br.com.fiap.techchallenge.infra.repository.dto.UserDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessorUseCase {

    private final IProcessor s3Consumer;
    private final IUserRepository userRepository;

    public ProcessorUseCase(IProcessor s3Consumer, IUserRepository userRepository) {
        this.s3Consumer = s3Consumer;
        this.userRepository = userRepository;
    }

    public void execute(ProcessorRequestDTO entrypoint) throws IOException {
        UserDTO user = userRepository.saveUser(
                new UserDTO(
                        entrypoint.getUser().getName(),
                        entrypoint.getUser().getEmail(),
                        UUID.fromString(entrypoint.getProtocol()),
                        LocalDateTime.now()));
        s3Consumer.processor(user);
    }
}
