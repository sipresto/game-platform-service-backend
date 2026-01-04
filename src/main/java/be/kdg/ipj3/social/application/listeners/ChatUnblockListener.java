package be.kdg.ipj3.social.application.listeners;

import be.kdg.ipj3.social.application.ChatService;
import be.kdg.ipj3.users.shared.ChatBlockEvent;
import be.kdg.ipj3.users.shared.ChatUnblockEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ChatUnblockListener {
    private final ChatService chatService;

    public ChatUnblockListener(ChatService chatService) {
        this.chatService = chatService;
    }

    @EventListener
    @Async
    void onUnblockReceived(ChatUnblockEvent event) {
        chatService.unblockChat(event.blocker(), event.blocked());
    }
}
