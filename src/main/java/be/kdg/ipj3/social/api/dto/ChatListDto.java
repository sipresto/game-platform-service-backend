package be.kdg.ipj3.social.api.dto;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.Message;

import java.time.Instant;
import java.util.UUID;

public record ChatListDto(
        UUID id,
        String name,
        MessageListDto lastMessage
) {
    public record MessageListDto(
            String content,
            Instant sentAt
    ) {
        public static MessageListDto fromDomain(Message message) {
            return new MessageListDto(
                    message.getContent(),
                    message.getSentAt()
            );
        }
    }

    public static ChatListDto fromDomain(Chat chat, String name) {
        MessageListDto lastMessage = MessageListDto.fromDomain(chat.getMessages().getLast());

        return new ChatListDto(
                chat.getId().id(),
                name,
                lastMessage
        );
    }
}
