package be.kdg.ipj3.chatbot.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ChatbotUploadResponseDto(boolean success,
                                       String code,
                                       String message,
                                       RagData data
) {
    public record RagData(
            String filename,
            @JsonProperty("document_id") UUID documentId,
            @JsonProperty("chunks_indexed") int chunksIndexed
    ) {
    }
}