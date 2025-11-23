package be.kdg.ipj3.users.domain.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * Public endpoint - no authentication required
     */
    @GetMapping("/public/health")
    public Map<String, String> publicEndpoint() {
        return Map.of("status", "ok", "access", "public");
    }

    /**
     * Authenticated endpoint - any logged-in user can access
     * &#064;AuthenticationPrincipal  gives you access to the JWT token
     */
    @GetMapping("/user/profile")
    public Map<String, Object> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> profile = new HashMap<>();

        profile.put("userId", jwt.getSubject());
        profile.put("username", jwt.getClaim("preferred_username"));
        profile.put("email", jwt.getClaim("email"));
        profile.put("name", jwt.getClaim("name"));
        profile.put("roles", extractRoles(jwt));

        return profile;
    }

    /**
     * Admin-only endpoint
     * Can use @PreAuthorize or configure in SecurityConfig
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<String> getAllUsers() {
        return List.of("user1", "user2", "admin");
    }

    /**
     * Game developer endpoint
     */
    @PreAuthorize("hasRole('GAME_DEVELOPER')")
    @PostMapping("/games/manage")
    public Map<String, String> manageGame(@RequestBody Map<String, Object> gameData) {
        return Map.of("message", "Game updated successfully");
    }

    /**
     * Multiple roles allowed
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'GAME_DEVELOPER')")
    @GetMapping("/games/stats")
    public Map<String, Object> getGameStats() {
        return Map.of("totalGames", 5, "activePlayers", 1000);
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