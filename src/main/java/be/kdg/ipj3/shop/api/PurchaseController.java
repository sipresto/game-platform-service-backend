package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Game;
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

    @PostMapping("/{id}")
    public ResponseEntity<PurchaseDto> buyGame(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        String keycloakId = jwt.getSubject();
        String keycloakUsername = jwt.getClaim("preferred_username");
        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        GameId gameId = new GameId(id);
        if (gameService.existsById(gameId)) {
            userService.addGame(user.getId(), gameId);
            Purchase purchase = purchaseService.buy(user.getId(), gameId);
            return ResponseEntity.ok(PurchaseDto.fromDomain(purchase));
        }
        else {
            return ResponseEntity.badRequest().build();
        }

    }
}
