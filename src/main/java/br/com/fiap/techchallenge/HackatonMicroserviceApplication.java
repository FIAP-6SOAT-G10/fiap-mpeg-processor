package br.com.fiap.techchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.fiap.techchallenge")
public class HackatonMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HackatonMicroserviceApplication.class, args);
    }
}