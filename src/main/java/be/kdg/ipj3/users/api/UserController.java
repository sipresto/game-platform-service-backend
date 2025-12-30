package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.api.dto.BlockedUserDto;
import be.kdg.ipj3.users.api.dto.PublicUserDto;
import be.kdg.ipj3.users.api.dto.UserDto;
import be.kdg.ipj3.users.application.ProfileImageService;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.domain.VisibilitySettings;
import be.kdg.ipj3.users.domain.VisibleField;
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
@RequestMapping("/api/platform/users")
public class UserController {
    private final UserService userService;
    private final ProfileImageService profileImageService;

    public UserController(UserService userService, ProfileImageService profileImageService) {
        this.userService = userService;
        this.profileImageService = profileImageService;
    }


    /**
     * Get current user's profile (full profile)
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
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
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UserDto.ProfileDto profileDto
    ) {
        String keycloakId = jwt.getSubject();
        userService.updateProfile(keycloakId, profileDto.displayName(), profileDto.bio());
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the global visibility settings of the user
     */
    @PutMapping("/privacy")
    public ResponseEntity<Void> updatePrivacySettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Visibility visibility
    ) {
        userService.updatePrivacySettingsByKeycloakId(visibility, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the visibility of a specific field
     */
    @PutMapping("/privacy/fields")
    public ResponseEntity<Void> updateFieldVisibility(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam VisibleField field,
            @RequestParam Visibility visibility
    ) {
        String keycloakId = jwt.getSubject();
        userService.updateFieldVisibility(keycloakId, field, visibility);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the current user's visibility settings
     */
    @GetMapping("/privacy/settings")
    public ResponseEntity<VisibilitySettings> getVisibilitySettings(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();
        VisibilitySettings settings = userService.getVisibilitySettings(keycloakId);
        return ResponseEntity.ok(settings);
    }

    /**
     * Unsecured endpoint that gets all users with visibility-aware data
     */
    @GetMapping
    public ResponseEntity<List<PublicUserDto>> getAllUsers(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<User> users = userService.findAllPublicUsers();
        User viewer = userService.findByKeycloakId(jwt.getSubject());
        var userDtos = users.stream()
                .filter(user -> !viewer.getBlockedUsers().contains(user.getId()))
                .map(user -> PublicUserDto.fromDomain(user, viewer))
                .toList();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Gets friends of current user
     */
    @GetMapping("/friends")
    public ResponseEntity<List<PublicUserDto>> getFriendsForUser(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findByKeycloakId(jwt.getSubject());
        var friends = userService.findFriends(user);
        var friendDtos = friends.stream()
                .map(friend -> PublicUserDto.fromDomain(friend, user))
                .toList();
        return ResponseEntity.ok(friendDtos);
    }

    /**
     * Gets a user by their ID with visibility-aware data
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PublicUserDto> getUserById(
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        User targetUser = userService.findById(userId);
        var viewer = userService.findByKeycloakId(jwt.getSubject());

        return ResponseEntity.ok(PublicUserDto.fromDomain(targetUser, viewer));
    }

    /**
     * Uploads or updates the profile image for the current user
     */
    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfileImage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("image") MultipartFile file) throws IOException {
        profileImageService.updateProfileImage(jwt.getSubject(), file);

        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the profile image for a user
     */
    @GetMapping("/profile-image/{userId}")
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
        profileImageService.removeProfileImage(jwt.getSubject());

        return ResponseEntity.noContent().build();
    }

    /**
     * Sends a friend request from one user to another
     */
    @PostMapping("{receiverId}/send-friend-request")
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
    @PutMapping("/{senderId}/friend-request/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.acceptFriendRequest(jwt.getSubject(), senderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Rejects a friend request
     */
    @PutMapping("/{senderId}/friend-request/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable UUID senderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.rejectFriendRequest(jwt.getSubject(), senderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{friendId}/remove")
    public ResponseEntity<Void> removeFriends(
            @PathVariable UUID friendId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.removeFriend(jwt.getSubject(), friendId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{blockId}/block")
    public ResponseEntity<Void> blockUser(
            @PathVariable UUID blockId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.blockUser(jwt.getSubject(), blockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<BlockedUserDto>> getBlockedUsers(
            @AuthenticationPrincipal Jwt jwt
    ) {
        var friends = userService.findBlockedUsers(jwt.getSubject()).stream().map(BlockedUserDto::fromDomain).toList();
        return ResponseEntity.ok(friends);
    }
}