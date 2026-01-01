package be.kdg.ipj3.chatbot.api.dto;

public record ChatbotResponseDto(
        String answer,
        boolean success
) {}