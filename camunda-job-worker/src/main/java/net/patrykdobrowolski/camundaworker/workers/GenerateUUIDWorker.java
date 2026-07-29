package net.patrykdobrowolski.camundaworker.workers;

import io.camunda.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GenerateUUIDWorker {

    @JobWorker(type = "generate_uuid")
    public Map<String, Object> generate() {
        return Map.of("uuid", java.util.UUID.randomUUID().toString());
    }
}
