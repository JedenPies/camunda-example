package net.patrykdobrowolski.example_camunda_connector;

import io.camunda.connector.api.annotation.Operation;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.annotation.Variable;
import io.camunda.connector.api.outbound.OutboundConnectorProvider;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.slf4j.Slf4j;

@OutboundConnector(
        name = "RESERVE_FLIGHT_CONNECTOR",
//        inputVariables = { "flightId", "seats" },
        type = "net.patrykdobrowolski:reserve-flight:1")
@ElementTemplate(id = "reserve-flight-connector", name = "Reserve Flight Connector")
@Slf4j
public class ReserveFlightConnector implements OutboundConnectorProvider {

    @Operation(name = "reserveFlight", id = "reserve-flight")
    public FlightResponseDto execute(@Variable FlightRequestDto flightRequest) throws Exception {

        log.info("making reservation");
        return FlightResponseDto.builder().status(ReservationStatus.FAILED).build();
    }
}
