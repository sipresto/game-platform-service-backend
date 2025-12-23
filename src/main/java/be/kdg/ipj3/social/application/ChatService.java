package be.kdg.ipj3.social.application;

import be.kdg.ipj3.social.api.dto.ChatDto;
import be.kdg.ipj3.social.api.dto.ChatListDto;
import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.social.domain.Message;
import be.kdg.ipj3.social.shared.InvalidChatRequestException;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final UsersApi usersApi;

    public ChatService(ChatRepository chatRepository, UsersApi usersApi) {
        this.chatRepository = chatRepository;
        this.usersApi = usersApi;
    }

    public List<ChatListDto> findChatListByKeycloakId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId);

        return chatRepository.findChatsByUserId(userId).stream()
                .map(chat -> {
                    String chatName = buildChatName(chat.getParticipants(), userId);

                    Message lastMessage = chat.getMessages().getLast();
                    String senderName = usersApi.findDisplayNameById(lastMessage.getSender());

                    return ChatListDto.fromDomain(chat, chatName, userId, senderName);
                })
                .toList();
    }

    public ChatDto findChatById(String keycloakId, ChatId chatId) {
        UserId currentUser = usersApi.findIdByKeycloakId(keycloakId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(chatId::notFound);

        if (!chat.getParticipants().contains(currentUser)) {
            throw new AccessDeniedException("User is not a participant of this chat");
        }

        Set<UserId> participantIds = chat.getParticipants();

        String chatName = buildChatName(participantIds, currentUser);

        var usernames = participantIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        usersApi::findDisplayNameById
                ));

        return ChatDto.fromDomain(chat, chatName, currentUser, usernames);
    }

    public ChatDto createChat(String keycloakId, Set<UserId> participants) {
        UserId currentUser = usersApi.findIdByKeycloakId(keycloakId);

        Set<UserId> participantIds = new HashSet<>(participants);
        participantIds.add(currentUser);

        participantIds.removeIf(u -> !usersApi.existsById(u.id()));

        if (participantIds.size() < 2) {
            throw new InvalidChatRequestException("Not enough participants");
        }

        Chat chat = chatRepository.findByExactParticipants(participantIds)
                .orElseGet(() -> {
                    Chat newChat = new Chat(null, participantIds);
                    chatRepository.save(newChat);
                    return newChat;
                });

        String chatName = buildChatName(participantIds, currentUser);

        var usernames = chat.getParticipants().stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        usersApi::findDisplayNameById
                ));

        return ChatDto.fromDomain(chat, chatName, currentUser, usernames);
    }

    public ChatDto sendMessage(String keycloakId, ChatId chatId, String content) {
        UserId currentUser = usersApi.findIdByKeycloakId(keycloakId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(chatId::notFound);

        chat.sendMessage(currentUser, content);
        chatRepository.save(chat);

        var usernames = chat.getParticipants().stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        usersApi::findDisplayNameById
                ));

        String chatName = buildChatName(chat.getParticipants(), currentUser);

        return ChatDto.fromDomain(chat, chatName, currentUser, usernames);
    }

    private String buildChatName(Set<UserId> participants, UserId currentUser) {
        return participants.stream()
                .filter(userId -> !userId.equals(currentUser))
                .map(usersApi::findDisplayNameById)
                .collect(Collectors.joining(", "));
    }
}
