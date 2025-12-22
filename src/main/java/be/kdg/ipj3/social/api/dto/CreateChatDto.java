package be.kdg.ipj3.social.api.dto;

import java.util.Set;
import java.util.UUID;

public record CreateChatDto(
        Set<UUID> participantUserIds
) {}
