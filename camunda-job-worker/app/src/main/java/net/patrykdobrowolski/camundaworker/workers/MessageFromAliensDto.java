package net.patrykdobrowolski.camundaworker.workers;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public class MessageFromAliensDto {

    private final String message;
}
