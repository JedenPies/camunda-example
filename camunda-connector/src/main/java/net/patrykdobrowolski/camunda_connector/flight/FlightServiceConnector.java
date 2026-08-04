package net.patrykdobrowolski.camunda_connector.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.annotation.Operation;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.annotation.Variable;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorProvider;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import lombok.extern.java.Log;
import net.patrykdobrowolski.camunda_connector.config.ExternalServicesConfiguration;
import net.patrykdobrowolski.camunda_connector.config.ObjectMapperConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@OutboundConnector(
        name = "FLIGHT_SERVICE_CONNECTOR",
        type = "net.patrykdobrowolski:flight-service:1")
@ElementTemplate(
        id = "flight-service-connector", name = "Flight Service Connector", description = "Allows to manage flight reservations",
        icon = "flight-service-connector.svg")
@Log
public class FlightServiceConnector implements OutboundConnectorProvider {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = ObjectMapperConfiguration.getObjectMapper();

    @Operation(name = "Reserve Flight", id = "reserve-flight")
    public FlighReservationOutput reserveFlight(@Variable FlightReservationInput input) throws Exception {
        HttpRequest request = prepareReservationRequest(input);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleReservationResponse(response);
    }

    @Operation(name = "Confirm Reservation", id = "confirm-reservation")
    public FlighReservationOutput confirmReservation(@Variable(name = "reservationNumber") String reservationNumber) throws Exception {
        HttpRequest request = prepareConfirmationRequest(reservationNumber);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleConfirmationResponse(response);
    }

    @Operation(name = "Cancel Reservation", id = "cancel-reservation")
    public FlighReservationOutput cancelReservation(@Variable(name = "reservationNumber") String reservationNumber) throws Exception {
        UUID reservationNumberUUID = UUID.fromString(reservationNumber);
        HttpRequest request = prepareCancellationRequest(reservationNumber);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleCancellationResponse(response, reservationNumberUUID);
    }

    private static HttpRequest prepareReservationRequest(FlightReservationInput input) throws JsonProcessingException {

        FlightReservationDto requestObject = FlightReservationDto.builder()
                .flightId(UUID.fromString(input.getFlightId()))
                .seats(input.getSeats())
                .build();
        return HttpRequest.newBuilder()
                .uri(URI.create(ExternalServicesConfiguration.getInstance().getFlightServiceUrl() + "/reservations"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestObject)))
                .build();
    }

    private static FlighReservationOutput handleReservationResponse(HttpResponse<String> response) throws JsonProcessingException {

        if (response.statusCode() == 200) {
            FlightReservationDto responseObject = objectMapper.readValue(response.body(), FlightReservationDto.class);
            if (responseObject.getStatus() == FlightReservationStatus.PENDING) {
                return FlighReservationOutput.builder()
                        .reservationNumber(responseObject.getId())
                        .status(FlightReservationStatus.PENDING)
                        .build();
            }
        }
        throw new ConnectorException("FLIGHT_RESERVATION_FAILED", "making reservation failed");
    }

    private static HttpRequest prepareConfirmationRequest(String reservationNumber) {

        return HttpRequest.newBuilder()
                .uri(URI.create(ExternalServicesConfiguration.getInstance().getFlightServiceUrl() + "/reservations/" + reservationNumber + "/confirmation"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
    }

    private static FlighReservationOutput handleConfirmationResponse(HttpResponse<String> response) throws JsonProcessingException {

        if (response.statusCode() == 200) {
            FlightReservationDto responseObject = objectMapper.readValue(response.body(), FlightReservationDto.class);
            if (responseObject.getStatus() == FlightReservationStatus.CONFIRMED) {
                return FlighReservationOutput.builder()
                        .reservationNumber(responseObject.getId())
                        .status(FlightReservationStatus.CONFIRMED)
                        .build();
            }
        }
        throw new ConnectorException("FLIGHT_CONFIRMATION_FAILED", "reservation confirmation failed");
    }


    private static HttpRequest prepareCancellationRequest(String reservationNumber) {
        return HttpRequest.newBuilder()
                .uri(URI.create(ExternalServicesConfiguration.getInstance().getFlightServiceUrl() + "/reservations/" + reservationNumber))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
    }

    private static FlighReservationOutput handleCancellationResponse(HttpResponse<String> response, UUID reservationNumberUUID) throws JsonProcessingException {
        if (response.statusCode() == 200) {
            FlightReservationDto responseObject = objectMapper.readValue(response.body(), FlightReservationDto.class);
            if (responseObject.getStatus() == FlightReservationStatus.CANCELLED) {
                return FlighReservationOutput.builder()
                        .reservationNumber(reservationNumberUUID)
                        .status(FlightReservationStatus.CANCELLED)
                        .build();
            }
        }
        throw new ConnectorException("FLIGHT_RESERVATION_CANCEL_FAILED", "canceling reservation failed");
    }
}
