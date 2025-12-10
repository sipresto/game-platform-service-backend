package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.api.dto.NotificationDto;
import be.kdg.ipj3.notifications.application.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
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
}
