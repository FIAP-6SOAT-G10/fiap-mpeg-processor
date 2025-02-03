package br.com.fiap.techchallenge.infra.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AWSConfig {

    static {
        System.setProperty("aws.region", "us-east-1");
    }

}
