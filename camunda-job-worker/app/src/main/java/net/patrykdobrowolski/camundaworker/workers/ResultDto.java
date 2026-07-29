package net.patrykdobrowolski.camundaworker.workers;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResultDto {

    private final String message;
    private final String code;
}
