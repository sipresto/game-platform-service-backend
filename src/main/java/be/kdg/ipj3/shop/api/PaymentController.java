package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/payment")
public class PaymentController {
    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/charge/{gameId}")
    public ResponseEntity<?> chargeGame(
            @PathVariable UUID gameId,
            @RequestBody Map<String, String> body
    ) {
        String stripeToken = body.get("stripeToken");

        try {
            Charge stripeCharge = stripeService.charge(gameId, stripeToken);
            return ResponseEntity.ok(Map.of(
                    "id", stripeCharge.getId(),
                    "status", stripeCharge.getStatus()
            ));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
