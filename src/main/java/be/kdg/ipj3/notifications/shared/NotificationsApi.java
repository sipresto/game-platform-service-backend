package be.kdg.ipj3.notifications.shared;

import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.UUID;

@NamedInterface
public interface NotificationsApi {
    void addNotification(String title, String description, NotificationType type, Instant sentAt, UUID userId,
                         UUID referenceId);
}
