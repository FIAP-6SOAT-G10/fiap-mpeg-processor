package br.com.fiap.techchallenge.infra.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    static {
        System.setProperty("aws.region", "us-east-1");
    }

}
