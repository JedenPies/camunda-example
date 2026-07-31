package net.patrykdobrowolski.example_camunda_connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.annotation.Operation;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.annotation.Variable;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorProvider;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.java.Log;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@OutboundConnector(
        name = "FLIGHT_SERVICE_CONNECTOR",
        type = "net.patrykdobrowolski:flight-service:1")
@ElementTemplate(id = "flight-service-connector", name = "Flight Service Connector")
@Log
public class FlightServiceConnector implements OutboundConnectorProvider {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(name = "Reserve Flight", id = "reserve-flight")
    public FlightResponseDto reserveFlight(@Variable FlightRequestDto flightRequest) throws Exception {

        ExternalFlightReservation requestObject = ExternalFlightReservation.builder()
                .flightId(UUID.fromString(flightRequest.getFlightId()))
                .seats(flightRequest.getSeats())
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://external-services:8080/api/flights/reservations"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestObject)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ExternalFlightReservation responseObject = objectMapper.readValue(response.body(), ExternalFlightReservation.class);
            if (responseObject.getStatus() == ExternalFlightReservationStatus.PENDING) {
                return FlightResponseDto.builder()
                        .reservationNumber(responseObject.getId())
                        .status(FlightReservationStatus.PENDING)
                        .build();
            }
        }
        throw new ConnectorException("FLIGHT_RESERVATION_FAILED", "making reservation failed");
    }

    @Operation(name = "Cancel Reservation", id = "cancel-reservation")
    public FlightResponseDto cancelReservation(@Variable(name = "reservationNumber") String reservationNumber) throws Exception {

        UUID reservationNumberUUID = UUID.fromString(reservationNumber);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://external-services:8080/api/flights/reservations/" + reservationNumber))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ExternalFlightReservation responseObject = objectMapper.readValue(response.body(), ExternalFlightReservation.class);
            if (responseObject.getStatus() == ExternalFlightReservationStatus.CANCELLED) {
                return FlightResponseDto.builder()
                        .reservationNumber(reservationNumberUUID)
                        .status(FlightReservationStatus.CANCELLED)
                        .build();
            }
        }
        throw new ConnectorException("FLIGHT_RESERVATION_CANCEL_FAILED", "canceling reservation failed");
    }
}
