package be.kdg.ipj3.users.api;

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

    public UserController(UserService userService) {
        this.userService = userService;
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
        String email = jwt.getClaim("email");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername, email);

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
     * Updates the visibility settings of the user
     */
    @PutMapping("/user/privacy")
    public ResponseEntity<Void> updatePrivacySettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Visibility visibility
    ) {
        String keycloakId = jwt.getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        userService.updatePrivacySettingsByKeycloakId(visibility, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Unsecured endpoint that gets all users whose accounts are public
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findAllPublicUsers() {
        var publicUsers = userService.findAllPublicUsers().stream().map(UserDto::fromDomain).toList();
        return ResponseEntity.ok(publicUsers);
    }

    /**
     * Gets friends of a user
     */
    @GetMapping("/friends")
    public ResponseEntity<List<UserDto>> findFriendsForUser(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findByKeycloakId(jwt.getSubject());
        var friends = userService.findFriendsAndSelf(user).stream().map(UserDto::fromDomain).toList();
        return ResponseEntity.ok(friends);
    }
}