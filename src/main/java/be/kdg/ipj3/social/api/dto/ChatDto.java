package be.kdg.ipj3.social.api.dto;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.Message;
import be.kdg.ipj3.users.shared.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChatDto(
        UUID id,
        String name,
        Set<ParticipantDto> participants,
        List<MessageDto> messages,
        boolean blocked
) {
    public record ParticipantDto(
            UUID id,
            String username
    ) {}

    public record MessageDto(
            UUID id,
            UUID senderId,
            boolean isMine,
            String content,
            Instant sentAt
    ) {}

    public static ChatDto fromDomain(
            Chat chat,
            String chatName,
            UserId currentUser,
            Map<UserId, String> usernames
    ) {
        Set<ParticipantDto> participants = chat.getParticipants().stream()
                .map(userId -> new ParticipantDto(
                        userId.id(),
                        usernames.get(userId)
                ))
                .collect(Collectors.toSet());

        List<Message> messages = chat.getMessages();

        int size = messages.size();
        int fromIndex = Math.max(0, size - 200);

        List<MessageDto> messageDtos = messages.subList(fromIndex, size).stream()
                .map(message -> new MessageDto(
                        message.getId().id(),
                        message.getSender().id(),
                        message.getSender().equals(currentUser),
                        message.getContent(),
                        message.getSentAt()
                ))
                .toList();

        return new ChatDto(
                chat.getId().id(),
                chat.getName() != null ? chat.getName() : chatName,
                participants,
                messageDtos,
                chat.isBlocked()
        );
    }
}
