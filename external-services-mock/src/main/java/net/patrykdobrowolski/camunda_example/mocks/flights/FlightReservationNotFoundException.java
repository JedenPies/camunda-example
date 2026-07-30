package net.patrykdobrowolski.camunda_example.mocks.flights;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FlightReservationNotFoundException extends Exception {
}
