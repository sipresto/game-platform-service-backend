package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
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

    public PurchaseController(PurchaseService purchaseService, UserService userService, GameService gameService) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.gameService = gameService;
    }

    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");
        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);
        return user.getId();
    }

    @PostMapping("/{gameIdParam}")
    public ResponseEntity<Map<String, String>> buyGame(
            @PathVariable UUID gameIdParam,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId userId = resolveUserId(token);
        GameId gameId = new GameId(gameIdParam);

        if (!gameService.existsById(gameId)) {
            return ResponseEntity.badRequest().build();
        }

        // Create payment and get checkout URL
        String checkoutUrl = purchaseService.initiatePurchase(userId, gameId);

        // Return checkout URL to frontend
        return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestParam("id") String paymentId) {
        // Mollie calls this endpoint when payment status changes
        purchaseService.handleWebhook(paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("id") String paymentId) {
        // User is redirected here after payment
        // Check if payment is completed and show appropriate message
        Purchase purchase = purchaseService.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        if (purchase.status().name().equals("PAID")) {
            userService.addGame(purchase.buyer(), purchase.game());
            return ResponseEntity.ok("Payment successful! Game added to your library.");
        }

        return ResponseEntity.ok("Payment pending. You will receive the game once payment is confirmed.");
    }
}