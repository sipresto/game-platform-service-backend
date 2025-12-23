package be.kdg.ipj3.social.api.dto;

import jakarta.validation.constraints.Size;

public record SendMessageDto(
        @Size(max = 2000) String content
) {}
