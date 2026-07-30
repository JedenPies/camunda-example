package net.patrykdobrowolski.camunda_example.mocks.offers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Transactional
public class OffersResource {

    private final OffersRepository offersRepository;

    @PostMapping
    public Offer addOffer(@RequestBody @Valid Offer offer) {
        offer.setId(UUID.randomUUID());
        return offersRepository.save(offer);
    }

    @GetMapping("{offerId}")
    public Offer retrieveOffer(@PathVariable UUID offerId) throws OfferNotFoundException {
        return offersRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException(offerId));
    }
}
