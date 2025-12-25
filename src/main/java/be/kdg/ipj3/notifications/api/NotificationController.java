package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.api.dto.NotificationDto;
import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
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

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
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

    // TODO: dto gebruiken?
    @GetMapping("/preferences")
    public ResponseEntity<Map<NotificationType, Map<NotificationChannel, Boolean>>> getPreferences(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserNotificationPreferences prefs = notificationService.getPreferencesByKeycloakId(jwt.getSubject());
        return ResponseEntity.ok(prefs.getPreferences());
    }

    // TODO: dto gebruiken?
    @PutMapping("/preferences/{notificationType}/{channel}")
    public ResponseEntity<Void> updatePreference(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable NotificationType notificationType,
            @PathVariable NotificationChannel channel,
            @RequestParam boolean enabled
    ) {
        notificationService.updatePreferences(jwt.getSubject(), notificationType, channel, enabled);
        return ResponseEntity.ok().build();
    }
}