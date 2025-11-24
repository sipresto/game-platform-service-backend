package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.api.SmallGameDto;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final GameService gameService;

    public UserController(
            UserService userService,
            GameService gameService
    ) {
        this.userService = userService;
        this.gameService = gameService;
    }

    /**
     * Get current user's profile
     * Automatically registers user on first login
     */
    @GetMapping("/user/profile")
    public ResponseEntity<ProfileDto> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String keycloakUsername = jwt.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);
        Profile profile = userService.getProfile(user.getId());

        return ResponseEntity.ok(ProfileDto.from(profile));
    }

    /**
     * Update current user's username
     */
    @PutMapping("/user/username")
    public ResponseEntity<Map<String, String>> updateUsername(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> request
    ) {
        String keycloakId = jwt.getSubject();
        String newUsername = request.get("username");

        if (newUsername == null || newUsername.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username cannot be empty"));
        }

        UserId userId = UserId.fromKeycloak(keycloakId);
        userService.updateUsername(userId, newUsername);

        return ResponseEntity.ok(Map.of("message", "Username updated successfully"));
    }

    /**
     * Get user details along with their owned games.
     */
    @GetMapping("/users/{id}/games")
    public ResponseEntity<UserDto> getUserWithGames(
            @PathVariable UUID id
    ) {
        User user = userService.getUserWithGames(new UserId(id.toString()));
        List<SmallGameDto> games = new ArrayList<>();

        if (!user.getOwnedGames().isEmpty()) {
            user.getOwnedGames().forEach(gameId -> games.add(SmallGameDto.from(gameService.getGameById(gameId))));
        }

        UserDto userDtos = UserDto.from(user, games);
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Helper method to extract roles from JWT
     */
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") != null) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles;
        }
        return List.of();
    }
}