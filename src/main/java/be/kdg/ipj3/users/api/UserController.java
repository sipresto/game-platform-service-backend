package be.kdg.ipj3.users.api;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Public endpoint - no authentication required
     */
    @GetMapping("/public/health")
    public Map<String, String> publicEndpoint() {
        return Map.of("status", "ok", "access", "public");
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
     * Admin-only endpoint
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<String> getAllUsers() {
        return List.of("user1", "user2", "admin");
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