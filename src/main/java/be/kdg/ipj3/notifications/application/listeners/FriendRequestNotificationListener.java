package be.kdg.ipj3.notifications.application.listeners;

import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.FriendRequestSentEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FriendRequestNotificationListener {
    private final NotificationService notificationService;

    public FriendRequestNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    @Async
    void onFriendRequest(FriendRequestSentEvent event) {
        notificationService.addNotification(
                event.username() + " sent a friend request",
                "Let's be friends!",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                event.receiverId(),
                event.senderId().id()
        );
    }
}
