package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.api.dto.NotificationDto;
import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UsersApi usersApi;

    public NotificationController(NotificationService notificationService, UsersApi usersApi) {
        this.notificationService = notificationService;
        this.usersApi = usersApi;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();

        List<NotificationDto> notificationDtos = notificationService.findAllNotifications(keycloakId)
                .stream().map(NotificationDto::fromDomain).toList();

        return ResponseEntity.ok(notificationDtos);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();
        notificationService.readNotification(id, keycloakId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preferences")
    public ResponseEntity<Map<NotificationType, Map<NotificationChannel, Boolean>>> getPreferences(
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        var user = usersApi.findIdByKeycloakId(keycloakId);

        UserNotificationPreferences prefs = notificationService.getPreferences(user.id());
        return ResponseEntity.ok(prefs.getAllPreferences());
    }

    @PutMapping("/preferences/{notificationType}/{channel}")
    public ResponseEntity<Void> updatePreference(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable NotificationType notificationType,
            @PathVariable NotificationChannel channel,
            @RequestParam boolean enabled) {
        String keycloakId = jwt.getSubject();
        var user = usersApi.findIdByKeycloakId(keycloakId);

        notificationService.updatePreferences(user.id(), notificationType, channel, enabled);
        return ResponseEntity.ok().build();
    }
}