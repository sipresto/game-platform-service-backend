package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.Optional;

@AggregateRoot
public class Notification {
    @Identity
    private final NotificationId id;
    private final String description;
    private final NotificationType type;
    private final Instant createdAt;
    private boolean read;
    private final UserId user;

    private Optional<UserNotificationPreferences> preferences;

    public Notification(NotificationId id, String description, NotificationType type,
                        Instant createdAt, boolean read, UserId user) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
        this.user = user;
        this.preferences = Optional.empty();
    }

    public Notification(String description, NotificationType type, UserId user) {
        this(NotificationId.create(), description, type, Instant.now(), false, user);
    }

    public boolean shouldSendViaChannel(NotificationChannel channel) {
        return preferences
                .map(prefs -> prefs.isEnabled(type, channel))
                .orElse(getDefaultForType(type, channel));
    }

    private boolean getDefaultForType(NotificationType type, NotificationChannel channel) {
        return type != NotificationType.YOUR_TURN;
    }

    public void setPreferences(UserNotificationPreferences preferences) {
        this.preferences = Optional.ofNullable(preferences);
    }

    public Optional<UserNotificationPreferences> getPreferences() {
        return preferences;
    }

    public NotificationId getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public NotificationType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public UserId getUser() {
        return user;
    }

    public void read() {
        this.read = true;
    }
}