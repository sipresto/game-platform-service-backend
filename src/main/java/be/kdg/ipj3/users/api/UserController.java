package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.application.ProfileImageService;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final ProfileImageService profileImageService;

    public UserController(UserService userService, ProfileImageService profileImageService) {
        this.userService = userService;
        this.profileImageService = profileImageService;
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
        var friends = userService.findFriends(user).stream().map(UserDto::fromDomain).toList();
        return ResponseEntity.ok(friends);
    }

    /**
     * Gets a user by their ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> findUserById(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserDto.fromDomain(user));
    }

    /**
     * Uploads or updates the profile image for the current user
     */
    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfileImage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("image") MultipartFile file) throws IOException {

        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String keycloakId = jwt.getSubject();
        profileImageService.updateProfileImage(keycloakId, file);

        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the profile image for a user
     */
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable UUID userId) {
        try {
            byte[] image = profileImageService.getProfileImage(new UserId(userId));
            String mimeType = profileImageService.getProfileImageMimeType(new UserId(userId));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Removes the profile image for the current user
     */
    @DeleteMapping("/me/delete/profile-image")
    public ResponseEntity<Void> removeProfileImage(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        profileImageService.removeProfileImage(keycloakId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Sends a friend request from one user to another
     */
    @PostMapping("/user/{receiverId}/send-friend-request")
    public ResponseEntity<Void> sendFriendRequest(
            @PathVariable UUID receiverId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.sendFriendRequest(receiverId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    /**
     * Accepts a friend request
     */
    @PutMapping("/user/{senderId}/friend-request/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var receiverId = userService.findIdByKeycloakId(jwt.getSubject());
        userService.acceptFriendRequest(receiverId.id(), senderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Rejects a friend request
     */
    @PutMapping("/user/{senderId}/friend-request/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var receiverId = userService.findIdByKeycloakId(jwt.getSubject());
        userService.rejectFriendRequest(receiverId.id(), senderId);
        return ResponseEntity.noContent().build();
    }
}