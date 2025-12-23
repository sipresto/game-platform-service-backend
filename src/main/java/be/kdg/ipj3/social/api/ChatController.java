package be.kdg.ipj3.social.api;

import be.kdg.ipj3.social.api.dto.ChatDto;
import be.kdg.ipj3.social.api.dto.ChatListDto;
import be.kdg.ipj3.social.api.dto.CreateChatDto;
import be.kdg.ipj3.social.api.dto.SendMessageDto;
import be.kdg.ipj3.social.application.ChatService;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<List<ChatListDto>> getChatsByKeycloakId(
            @AuthenticationPrincipal Jwt token
    ) {
        return ResponseEntity.ok(chatService.findChatListByKeycloakId(token.getSubject()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getChatById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        return ResponseEntity.ok(chatService.findChatById(token.getSubject(), new ChatId(id)));
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(
            @RequestBody CreateChatDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        Set<UserId> participants = dto.participantUserIds().stream()
                .map(UserId::new)
                .collect(Collectors.toSet());

        ChatDto chatDto = chatService.createChat(token.getSubject(), participants);

        return ResponseEntity.created(URI.create("/api/chats/" + chatDto.id()))
                .body(chatDto);
    }

    @PostMapping("/{id}/message")
    public ResponseEntity<Void> sendMessage(
            @PathVariable UUID id,
            @RequestBody SendMessageDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        chatService.sendMessage(token.getSubject(), new ChatId(id), dto.content());

        return ResponseEntity.ok().build();
    }
}
