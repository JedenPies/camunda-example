package net.patrykdobrowolski.camundaworker.workers;

import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class ExampleJobWorker {

    private final CamundaClient client;

    public ExampleJobWorker(CamundaClient client) throws ExecutionException, InterruptedException {
        this.client = client;
//        this.client.newAssignClientToGroupCommand().clientId("example-client").groupId("example-group").send().get();
    }

    @JobWorker(type = "example_job", enabled = true)

//    @JobWorker
    public ResultDto handleExampleJob(@Variable(name = "message_from_aliens") MessageFromAliensDto message) {
        log.info("Example job executed");
        return ResultDto.builder().message("message blah").code("code blah").build();
    }
}
