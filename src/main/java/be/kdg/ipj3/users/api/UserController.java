package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.api.dto.LibraryGameDto;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import jakarta.validation.Valid;
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
    @GetMapping("/user")
    public ResponseEntity<UserDto> findUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();
        String keycloakUsername = jwt.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return ResponseEntity.ok(UserDto.fromDomain(user));
    }

    /**
     * Update current user's profile
     */
    @PutMapping("/user/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UserDto.ProfileDto profileDto
    ) {
        String keycloakId = jwt.getSubject();

        userService.updateProfile(keycloakId, profileDto.displayName(), profileDto.bio());

        return ResponseEntity.ok().build();
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

    /**
     * Updates the visibility settings of the user
     */
    @PutMapping("/user/privacy")
    public ResponseEntity<Void> updatePrivacySettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Visibility visibility
    ) {
        String keycloakId = jwt.getSubject();
        User user = userService.findByKeycloakId(keycloakId).orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
        userService.updatePrivacySettingsByKeycloakId(visibility, user);
        return ResponseEntity.ok().build();
    }
}