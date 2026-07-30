package net.patrykdobrowolski.camunda_example.mocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ExternalServicesMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalServicesMockApplication.class, args);
    }
}
