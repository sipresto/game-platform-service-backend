package be.kdg.ipj3.notifications.shared;

import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
public interface NotificationsApi {
    void addNotification(String description, NotificationType type, UUID userId);
}
