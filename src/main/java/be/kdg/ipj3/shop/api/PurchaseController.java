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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: Not auto-register users, but verify if they are registered, using commented code below
    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return user.getId();
    }

    /*private UserId resolveUserId(Jwt token) {
        return userService
                .findByKeycloakId(token.getSubject())
                .map(User::getId)
                .orElseThrow(() -> new UserNotFoundException(token.getSubject()));
    }*/

    @PostMapping("/{gameIdParam}")
    public ResponseEntity<PurchaseDto> buyGame(
            @PathVariable UUID gameIdParam,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId userId = resolveUserId(token);

        GameId gameId = new GameId(gameIdParam);

        if (!gameService.existsById(gameId)) {
            return ResponseEntity.badRequest().build();
        }

        userService.addGame(userId, gameId);
        Purchase purchase = purchaseService.buy(userId, gameId);

        return ResponseEntity.ok(PurchaseDto.fromDomain(purchase));
    }
}
