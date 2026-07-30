package net.patrykdobrowolski.camunda_example.mocks.offers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@RequiredArgsConstructor
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OfferNotFoundException extends Exception {

    private final UUID offerId;
}
