package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@AggregateRoot
public class Notification {
    @Identity
    private final NotificationId id;
    private final String title;
    private final String description;
    private final NotificationType type;
    private final Instant createdAt;
    private boolean read;
    private final UserId user;
    private final UUID referenceId;
    // TODO: is al nullable, best deze verwijderen en de preference logica in service afhandelen, want preferences
    //  worden bij een user opgeslagen en hoeven niet nog extra hier te worden toegevoegd
    private Optional<UserNotificationPreferences> preferences;

    public Notification(NotificationId id, String title, String description, NotificationType type,
                        Instant createdAt, boolean read, UserId user, UUID referenceId) {
        this.id = id;
        this.title = title == null ? generateTitle(type) : title;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
        this.user = user;
        this.referenceId = referenceId;
        this.preferences = Optional.empty();
    }

    public Notification(String description, NotificationType type, UserId user) {
        this(NotificationId.create(), null, description, type, Instant.now(), false, user, null);
    }

    public Notification(String title, String description, NotificationType type, Instant sentAt, UserId user, UUID referenceId) {
        this(NotificationId.create(), title, description, type, sentAt, false, user, referenceId);
    }

    public boolean shouldSendViaChannel(NotificationChannel channel) {
        return preferences
                .map(prefs -> prefs.isEnabled(type, channel))
                .orElse(getDefaultForType(type, channel));
    }

    // TODO: maybe restructure this to make it more readable or always have default preferences so this method can be removed
    private boolean getDefaultForType(NotificationType type, NotificationChannel channel) {
        if (type == NotificationType.CHAT_MESSAGE && channel != NotificationChannel.IN_APP) {
            return false;
        }

        return true;
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

    public String getTitle() {
        return title;
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

    public UUID getReferenceId() {
        return referenceId;
    }

    public void read() {
        this.read = true;
    }

    private String generateTitle(NotificationType type) {
        return switch (type) {
            case FRIEND_REQUEST -> "You have a new friend request!";
            case ACHIEVEMENT -> "New achievement unlocked!";
            case YOUR_TURN -> "Your turn!";
            default -> "Notification";
        };
    }
}