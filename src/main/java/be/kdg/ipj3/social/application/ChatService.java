package be.kdg.ipj3.social.application;

import be.kdg.ipj3.lobbies.shared.LobbyId;
import be.kdg.ipj3.social.api.dto.ChatDto;
import be.kdg.ipj3.social.api.dto.ChatListDto;
import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.social.domain.Message;
import be.kdg.ipj3.social.shared.ChatsApi;
import be.kdg.ipj3.social.shared.InvalidChatRequestException;
import be.kdg.ipj3.social.shared.ChatMessageSentEvent;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService implements ChatsApi {
    private final ChatRepository chatRepository;
    private final UsersApi usersApi;
    private final ApplicationEventPublisher eventPublisher;

    public ChatService(ChatRepository chatRepository, UsersApi usersApi, ApplicationEventPublisher eventPublisher) {
        this.chatRepository = chatRepository;
        this.usersApi = usersApi;
        this.eventPublisher = eventPublisher;
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

        if (usersApi.checkBlock(participantIds)) {
            throw new InvalidChatRequestException("One of the users has blocked another user in this chat");
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

        var recipients = chat.getParticipants().stream()
                .filter(id -> !id.equals(currentUser))
                .collect(Collectors.toSet());

        eventPublisher.publishEvent(new ChatMessageSentEvent(
                chat.getId(),
                currentUser,
                recipients,
                content,
                chat.getLastMessageAt()
        ));

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

    @Override
    public void sendInvite(String keycloakId, String gameName, String password, LobbyId lobbyId, UserId senderId, UserId receiverId) {
        Set<UserId> participantIds = new HashSet<>();
        participantIds.add(senderId);
        participantIds.add(receiverId);

        if (usersApi.checkBlock(participantIds)) {
            throw new InvalidChatRequestException("One of the users has blocked another user in this chat");
        }

        var chat = chatRepository.findByExactParticipants(participantIds)
                .orElseGet(() -> {
                    Chat newChat = new Chat(null, participantIds);
                    chatRepository.save(newChat);
                    return newChat;
                });

        String roomName = "Join me in room " + lobbyId.id().toString().toUpperCase().substring(30);
        String passwordText = (password == null || password.isEmpty()) ? "It's public" : "The password is \"" + password + "\"";

        String message = """
                Come play %s with me!
                %s! %s!
                """.formatted(gameName, roomName, passwordText);
        sendMessage(keycloakId, chat.getId(), message);
    }

    public void blockChat(UserId blockerId, UserId blockedId) {
        Set<UserId> participantIds = new HashSet<>();
        participantIds.add(blockerId);
        participantIds.add(blockedId);


        var chat = chatRepository.findByExactParticipants(participantIds);

        if (chat.isEmpty()) {
            return;
        }

        chat.get().block();
        chatRepository.save(chat.get());
    }

    public void unblockChat(UserId unblockerId, UserId unblockedId) {
        Set<UserId> participantIds = new HashSet<>();
        participantIds.add(unblockerId);
        participantIds.add(unblockedId);


        var chat = chatRepository.findByExactParticipants(participantIds);

        if (chat.isEmpty()) {
            return;
        }
        chat.get().unblock();
        chatRepository.save(chat.get());
    }
}
