package br.com.fiap.techchallenge.infra.config;

import br.com.fiap.techchallenge.application.gateways.IProcessor;
import br.com.fiap.techchallenge.application.gateways.IUserDetailRepository;
import br.com.fiap.techchallenge.application.gateways.IUserRepository;
import br.com.fiap.techchallenge.application.usecases.AmazonSQSUseCase;
import br.com.fiap.techchallenge.application.usecases.ProcessorOutUseCase;
import br.com.fiap.techchallenge.application.usecases.ProcessorUseCase;
import br.com.fiap.techchallenge.infra.entrypoints.bucket.ProcessorFilesS3;
import br.com.fiap.techchallenge.infra.gateways.UserDetailRepositoryGateway;
import br.com.fiap.techchallenge.infra.gateways.UserRepositoryGateway;
import br.com.fiap.techchallenge.infra.mapper.UserDetailMapper;
import br.com.fiap.techchallenge.infra.mapper.UserMapper;
import br.com.fiap.techchallenge.infra.repository.UserDetailRepository;
import br.com.fiap.techchallenge.infra.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public IProcessor processorFilesS3(IUserRepository userRepository, IUserDetailRepository detailRepository, ProcessorOutUseCase processorOut, AmazonSQSUseCase amazonSQSUseCase) {
        return new ProcessorFilesS3(userRepository, detailRepository, processorOut, amazonSQSUseCase);
    }

    @Bean
    public IUserRepository builderUserRepository(UserRepository repository, UserMapper mapper) {
        return new UserRepositoryGateway(repository, mapper);  // Replace with your actual implementation of IUserRepository
    }

    @Bean
    public ProcessorUseCase processorUseCase(IProcessor s3Consumer, IUserRepository userRepository) {
        return new ProcessorUseCase(s3Consumer, userRepository);
    }

    @Bean
    public IUserDetailRepository userDetailRepository(UserDetailRepository repository, UserMapper userMapper, UserDetailMapper mapper) {
        return new UserDetailRepositoryGateway(repository, mapper, userMapper);  // Replace with your actual implementation of IUserRepository
    }

    @Bean
    public UserMapper buildUserMapper(){
        return new UserMapper();
    }

    @Bean
    public UserDetailMapper buildUserDetailMapper(){
        return new UserDetailMapper();
    }

    @Bean
    public ProcessorOutUseCase buildProcessorOutUseCase(){
       return new ProcessorOutUseCase();
    }

    @Bean
    public AmazonSQSUseCase buildAmazonSQSUseCase() {
        return new AmazonSQSUseCase();
    }
}
