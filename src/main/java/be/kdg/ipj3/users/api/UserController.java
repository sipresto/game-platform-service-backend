package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.api.dto.SendFriendRequestDto;
import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Sends a friend request from one user to another
     */
    @PostMapping("/sendFriendRequest")
    public ResponseEntity<Void> sendFriendRequest(
            @RequestBody @Valid SendFriendRequestDto sendFriendRequestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserId currentUserId = userService.findIdByKeycloakId(jwt.getSubject());
        if (!currentUserId.id().equals(sendFriendRequestDto.senderId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.sendFriendRequest(sendFriendRequestDto.receiverId(), sendFriendRequestDto.senderId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Accepts a friend request
     */
    @PutMapping("/acceptFriendRequest/{userId}")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable("userId") UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var receiverId = userService.findIdByKeycloakId(jwt.getSubject());
        userService.acceptFriendRequest(receiverId.id(), senderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Rejects a friend request
     */
    @PutMapping("/rejectFriendRequest/{userId}")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable("userId") UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var receiverId = userService.findIdByKeycloakId(jwt.getSubject());
        userService.rejectFriendRequest(receiverId.id(), senderId);
        return ResponseEntity.noContent().build();
    }
}