package be.kdg.ipj3.notifications.infrastructure.jpa;

import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "user_notification_preferences")
public class JpaUserNotificationPreferencesEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private boolean friendRequestInApp;

    @Column(nullable = false)
    private boolean friendRequestEmail;

    @Column(nullable = false)
    private boolean achievementInApp;

    @Column(nullable = false)
    private boolean achievementEmail;

    @Column(nullable = false)
    private boolean yourTurnInApp;

    @Column(nullable = false)
    private boolean yourTurnEmail;

    @Column(nullable = false)
    private boolean chatMessageInApp;

    @Column(nullable = false)
    private boolean chatMessageEmail;

    protected JpaUserNotificationPreferencesEntity() {}

    public JpaUserNotificationPreferencesEntity(UUID userId, boolean friendRequestInApp, boolean friendRequestEmail,
                                                boolean achievementInApp, boolean achievementEmail, boolean yourTurnInApp,
                                                boolean yourTurnEmail, boolean chatMessageInApp, boolean chatMessageEmail) {
        this.userId = userId;
        this.friendRequestInApp = friendRequestInApp;
        this.friendRequestEmail = friendRequestEmail;
        this.achievementInApp = achievementInApp;
        this.achievementEmail = achievementEmail;
        this.yourTurnInApp = yourTurnInApp;
        this.yourTurnEmail = yourTurnEmail;
        this.chatMessageInApp = chatMessageInApp;
        this.chatMessageEmail = chatMessageEmail;
    }

    public static JpaUserNotificationPreferencesEntity fromDomain(UserNotificationPreferences preferences) {
        java.util.Map<NotificationType, java.util.Map<NotificationChannel, Boolean>> prefs = preferences.getPreferences();

        return new JpaUserNotificationPreferencesEntity(
                preferences.getUserId().id(),
                prefs.get(NotificationType.FRIEND_REQUEST).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.FRIEND_REQUEST).get(NotificationChannel.EMAIL),
                prefs.get(NotificationType.ACHIEVEMENT).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.ACHIEVEMENT).get(NotificationChannel.EMAIL),
                prefs.get(NotificationType.YOUR_TURN).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.YOUR_TURN).get(NotificationChannel.EMAIL),
                prefs.get(NotificationType.CHAT_MESSAGE).get(NotificationChannel.IN_APP),
                prefs.get(NotificationType.CHAT_MESSAGE).get(NotificationChannel.EMAIL)
        );
    }

    public UserNotificationPreferences toDomain() {
        Map<NotificationType, Map<NotificationChannel, Boolean>> prefMap = new EnumMap<>(NotificationType.class);

        Map<NotificationChannel, Boolean> friendRequestMap = new EnumMap<>(NotificationChannel.class);
        friendRequestMap.put(NotificationChannel.IN_APP, friendRequestInApp);
        friendRequestMap.put(NotificationChannel.EMAIL, friendRequestEmail);
        prefMap.put(NotificationType.FRIEND_REQUEST, friendRequestMap);

        Map<NotificationChannel, Boolean> achievementMap = new EnumMap<>(NotificationChannel.class);
        achievementMap.put(NotificationChannel.IN_APP, achievementInApp);
        achievementMap.put(NotificationChannel.EMAIL, achievementEmail);
        prefMap.put(NotificationType.ACHIEVEMENT, achievementMap);

        Map<NotificationChannel, Boolean> yourTurnMap = new EnumMap<>(NotificationChannel.class);
        yourTurnMap.put(NotificationChannel.IN_APP, yourTurnInApp);
        yourTurnMap.put(NotificationChannel.EMAIL, yourTurnEmail);
        prefMap.put(NotificationType.YOUR_TURN, yourTurnMap);

        Map<NotificationChannel, Boolean> chatMessageMap = new EnumMap<>(NotificationChannel.class);
        chatMessageMap.put(NotificationChannel.IN_APP, chatMessageInApp);
        chatMessageMap.put(NotificationChannel.EMAIL, chatMessageEmail);
        prefMap.put(NotificationType.CHAT_MESSAGE, chatMessageMap);

        return new UserNotificationPreferences(new UserId(userId), prefMap);
    }
}