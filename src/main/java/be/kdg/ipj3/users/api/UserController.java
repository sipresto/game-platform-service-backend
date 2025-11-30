package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.api.dto.LibraryGameDto;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<UserDto> findUserProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();
        String keycloakUsername = jwt.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return ResponseEntity.ok(UserDto.fromDomain(user));
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

        userService.updateUsername(keycloakId, newUsername);

        return ResponseEntity.ok(Map.of("message", "Username updated successfully"));
    }

    /**
     * Get the owned games of a user.
     */
    @GetMapping("/user/{id}/games")
    public ResponseEntity<List<LibraryGameDto>> findOwnedGamesByKeycloakId(
            @PathVariable String id
    ) {
        List<GameId> ownedGameIds = userService.findOwnedGamesByKeycloakId(id);
        List<LibraryGameDto> dtos = gameService.findGamesByIds(ownedGameIds).stream()
                .map(LibraryGameDto::from)
                .toList();

        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }

        return ResponseEntity.ok(dtos);
    }
}