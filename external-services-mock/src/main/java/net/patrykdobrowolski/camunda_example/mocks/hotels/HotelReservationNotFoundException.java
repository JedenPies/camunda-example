package net.patrykdobrowolski.camunda_example.mocks.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class HotelReservationNotFoundException extends Exception {

    private final UUID reservationId;
}
