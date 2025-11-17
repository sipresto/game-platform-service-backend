package be.kdg.ipj3.shared.domain.profile;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Notification {
    @Identity
    public final NotificationId id;
    public final String description;
    public final NotificationType type;
    public final Instant createdAt;
    public boolean read;

    public Notification(String description, NotificationType type) {
        this.description = description;
        this.type = type;
        this.id = new NotificationId(UUID.randomUUID());
        this.createdAt = Instant.now();
        read = false;
    }

    //for jpa
    public Notification(NotificationId id, String description, NotificationType type, Instant createdAt, boolean read) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
    }

    public NotificationId id() {
        return id;
    }

    public String description() {
        return description;
    }

    public NotificationType type() {
        return type;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void read() {
        this.read = true;
    }
}
