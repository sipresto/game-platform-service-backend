package be.kdg.ipj3.social.shared;

import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.event.annotation.DomainEvent;

import java.time.Instant;
import java.util.Set;

@DomainEvent
public record ChatMessageSentEvent(
        ChatId chatId,
        UserId senderId,
        Set<UserId> recipients,
        String content,
        Instant sentAt
) {}
