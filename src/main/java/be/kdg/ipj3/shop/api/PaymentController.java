package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shop.infrastructure.stripe.ChargeRequest;
import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final GameService gameService;
    private final StripeService stripeService;

    public PaymentController(GameService gameService, StripeService stripeService) {
        this.gameService = gameService;
        this.stripeService = stripeService;
    }

    @PostMapping("/charge/{gameId}")
    public ResponseEntity<?> chargeGame(
            @PathVariable UUID gameId,
            @RequestBody Map<String, String> body
    ) {
        String stripeToken = body.get("stripeToken");

        try {
            Game game = gameService.findGameById(new GameId(gameId));
            Money total = game.getPrice();
            int amountInCents = total.amount().intValue() * 100;

            ChargeRequest charge = new ChargeRequest(
                    "Payment for: " + game.getName(),
                    amountInCents,
                    total.currency(),
                    stripeToken
            );

            Charge stripeCharge = stripeService.charge(charge);
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
