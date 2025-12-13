package be.kdg.ipj3.notifications.infrastructure.jpa;

import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_notification_preferences")
public class JpaUserNotificationPreferencesEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "friend_request_in_app")
    private Boolean friendRequestInApp;

    @Column(name = "friend_request_email")
    private Boolean friendRequestEmail;

    @Column(name = "achievement_in_app")
    private Boolean achievementInApp;

    @Column(name = "achievement_email")
    private Boolean achievementEmail;

    @Column(name = "your_turn_in_app")
    private Boolean yourTurnInApp;

    @Column(name = "your_turn_email")
    private Boolean yourTurnEmail;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    protected JpaUserNotificationPreferencesEntity() {}

    public JpaUserNotificationPreferencesEntity(UUID userId,
                                                Boolean friendRequestInApp, Boolean friendRequestEmail,
                                                Boolean achievementInApp, Boolean achievementEmail,
                                                Boolean yourTurnInApp, Boolean yourTurnEmail) {
        this.userId = userId;
        this.friendRequestInApp = friendRequestInApp;
        this.friendRequestEmail = friendRequestEmail;
        this.achievementInApp = achievementInApp;
        this.achievementEmail = achievementEmail;
        this.yourTurnInApp = yourTurnInApp;
        this.yourTurnEmail = yourTurnEmail;
    }

    public static JpaUserNotificationPreferencesEntity fromDomain(UserNotificationPreferences preferences) {
        java.util.Map<NotificationType, java.util.Map<NotificationChannel, Boolean>> prefs = preferences.getAllPreferences();

        return new JpaUserNotificationPreferencesEntity(
                preferences.getUserId().id(),
                prefs.get(NotificationType.FRIEND_REQUEST).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.FRIEND_REQUEST).get(NotificationChannel.EMAIL),
                prefs.get(NotificationType.ACHIEVEMENT).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.ACHIEVEMENT).get(NotificationChannel.EMAIL),
                prefs.get(NotificationType.YOUR_TURN).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.YOUR_TURN).get(NotificationChannel.EMAIL)
        );
    }

    public UserNotificationPreferences toDomain() {
        java.util.Map<NotificationType, java.util.Map<NotificationChannel, Boolean>> prefMap =
                new java.util.EnumMap<>(NotificationType.class);

        // FRIEND_REQUEST
        java.util.Map<NotificationChannel, Boolean> friendRequestMap = new java.util.EnumMap<>(NotificationChannel.class);
        friendRequestMap.put(NotificationChannel.IN_APP,
                friendRequestInApp != null ? friendRequestInApp : true);
        friendRequestMap.put(NotificationChannel.EMAIL,
                friendRequestEmail != null ? friendRequestEmail : true);
        prefMap.put(NotificationType.FRIEND_REQUEST, friendRequestMap);

        // ACHIEVEMENT
        java.util.Map<NotificationChannel, Boolean> achievementMap = new java.util.EnumMap<>(NotificationChannel.class);
        achievementMap.put(NotificationChannel.IN_APP,
                achievementInApp != null ? achievementInApp : true);
        achievementMap.put(NotificationChannel.EMAIL,
                achievementEmail != null ? achievementEmail : true);
        prefMap.put(NotificationType.ACHIEVEMENT, achievementMap);

        // YOUR_TURN
        java.util.Map<NotificationChannel, Boolean> yourTurnMap = new java.util.EnumMap<>(NotificationChannel.class);
        yourTurnMap.put(NotificationChannel.IN_APP,
                yourTurnInApp != null ? yourTurnInApp : false);
        yourTurnMap.put(NotificationChannel.EMAIL,
                yourTurnEmail != null ? yourTurnEmail : false);
        prefMap.put(NotificationType.YOUR_TURN, yourTurnMap);

        return new UserNotificationPreferences(new UserId(userId), prefMap);
    }
}