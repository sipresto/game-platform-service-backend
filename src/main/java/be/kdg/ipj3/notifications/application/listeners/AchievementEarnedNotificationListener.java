package be.kdg.ipj3.notifications.application.listeners;

import be.kdg.ipj3.achievementProgress.shared.AchievementEarnedEvent;
import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.shared.NotificationType;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class AchievementEarnedNotificationListener {
    private final NotificationService notificationService;

    AchievementEarnedNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    @Async
    void onAchievementEarned(AchievementEarnedEvent event) {
        notificationService.addNotification(
                null,
                event.description(),
                NotificationType.ACHIEVEMENT,
                event.earnedAt(),
                event.playerId(),
                null
        );
    }
}
