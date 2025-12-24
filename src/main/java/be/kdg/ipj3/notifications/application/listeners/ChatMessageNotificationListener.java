package be.kdg.ipj3.notifications.application.listeners;

import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.social.shared.ChatMessageSentEvent;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class ChatMessageNotificationListener {

    private final NotificationService notificationService;
    private final UsersApi usersApi;

    ChatMessageNotificationListener(NotificationService notificationService, UsersApi usersApi) {
        this.notificationService = notificationService;
        this.usersApi = usersApi;
    }

    @EventListener
    @Async
    void onChatMessageSent(ChatMessageSentEvent event) {
        String senderName = usersApi.findDisplayNameById(event.senderId());
        String preview = event.content().length() > 20
                ? event.content().substring(0, 20) + "…"
                : event.content();

        for (UserId recipient : event.recipients()) {
            notificationService.addNotification(
                    senderName + " sent you a message",
                    preview,
                    NotificationType.CHAT_MESSAGE,
                    event.sentAt(),
                    recipient.id(),
                    event.chatId().id()
            );
        }
    }
}
