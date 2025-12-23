package be.kdg.ipj3.social.api.dto;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.Message;
import be.kdg.ipj3.users.shared.UserId;

import java.time.Instant;
import java.util.UUID;

public record ChatListDto(
        UUID id,
        String name,
        MessageListDto lastMessage
) {
    public record MessageListDto(
            String content,
            Instant sentAt,
            boolean isMine,
            String senderName
    ) {
        public static MessageListDto fromDomain(Message message, UserId currentUser, String senderName) {
            return new MessageListDto(
                    message.getContent(),
                    message.getSentAt(),
                    message.getSender().equals(currentUser),
                    senderName
            );
        }
    }

    public static ChatListDto fromDomain(Chat chat, String chatName, UserId currentUser, String lastSenderName) {
        MessageListDto lastMessage = MessageListDto.fromDomain(chat.getMessages().getLast(), currentUser, lastSenderName);

        return new ChatListDto(
                chat.getId().id(),
                chat.getName() != null ? chat.getName() : chatName,
                lastMessage
        );
    }
}
