package net.patrykdobrowolski.camundaworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CamundaJobWorkerApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaJobWorkerApplication.class, args);
    }
}
