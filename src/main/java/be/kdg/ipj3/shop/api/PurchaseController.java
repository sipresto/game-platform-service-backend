package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shop.api.dto.PurchaseDto;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
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
    private final UsersApi usersApi;
    private final GamesApi gamesApi;
    private final StripeService stripeService;

    public PurchaseController(PurchaseService purchaseService, UsersApi usersApi, GamesApi gamesApi, StripeService stripeService) {
        this.purchaseService = purchaseService;
        this.usersApi = usersApi;
        this.gamesApi = gamesApi;
        this.stripeService = stripeService;
    }

    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");

        User user = usersApi.registerOrGetUser(keycloakId, keycloakUsername);

        return user.getId();
    }

    @PostMapping("/{gameIdParam}")
    public ResponseEntity<PurchaseDto> buyGame(
            @PathVariable UUID gameIdParam,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal Jwt token
    ) {
        String chargeId = requestBody.get("chargeId");
        var gameId = new GameId(gameIdParam);

        if (chargeId == null || chargeId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }


        if (!gamesApi.existsById(gameIdParam)) {
            return ResponseEntity.notFound().build();
        }

        UserId userId = resolveUserId(token);

        try {
            Charge charge = stripeService.retrieve(chargeId);

            if (!charge.getPaid() || !"succeeded".equals(charge.getStatus())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
            }


            usersApi.addGame(userId, gameId);

            Purchase purchase = purchaseService.buy(userId, gameId, chargeId);

            return ResponseEntity.ok(PurchaseDto.fromDomain(purchase));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
