package be.kdg.ipj3.social.application.listeners;

import be.kdg.ipj3.social.application.ChatService;
import be.kdg.ipj3.users.shared.ChatBlockEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ChatBlockListener {
    private final ChatService chatService;

    public ChatBlockListener(ChatService chatService) {
        this.chatService = chatService;
    }

    @EventListener
    @Async
    void onBlockReceived(ChatBlockEvent event) {
        chatService.blockChat(event.blocker(), event.blocked());
    }
}
