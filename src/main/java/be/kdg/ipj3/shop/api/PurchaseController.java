package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.api.dto.PurchaseDto;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final UserService userService;
    private final GameService gameService;
    private final StripeService stripeService;

    public PurchaseController(PurchaseService purchaseService, UserService userService, GameService gameService, StripeService stripeService) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.gameService = gameService;
        this.stripeService = stripeService;
    }

    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return user.getId();
    }

    @PostMapping("/{gameIdParam}")
    public ResponseEntity<PurchaseDto> buyGame(
            @PathVariable UUID gameIdParam,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal Jwt token
    ) {
        //TODO Has to move to the service!!!!
        String chargeId = requestBody.get("chargeId");
        GameId gameId = new GameId(gameIdParam);


        if (chargeId == null || chargeId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }


        if (!gameService.existsById(gameId)) {
            return ResponseEntity.badRequest().build();
        }

        UserId userId = resolveUserId(token);

        try {
            Charge charge = stripeService.retrieve(chargeId);

            if (!charge.getPaid() || !"succeeded".equals(charge.getStatus())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
            }

            userService.addGame(userId, gameId);

            Purchase purchase = purchaseService.buy(userId, gameId, chargeId);

            return ResponseEntity.ok(PurchaseDto.fromDomain(purchase));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
