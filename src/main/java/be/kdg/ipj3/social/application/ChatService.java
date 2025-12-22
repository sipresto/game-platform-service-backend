package be.kdg.ipj3.social.application;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;

import java.util.HashSet;
import java.util.Set;

public class ChatService {
    private final ChatRepository chatRepository;
    private final UsersApi usersApi;

    public ChatService(ChatRepository chatRepository, UsersApi usersApi) {
        this.chatRepository = chatRepository;
        this.usersApi = usersApi;
    }

    public Chat createChat(String callerKeycloakId, Set<UserId> participants) {
        Set<UserId> participantIds = new HashSet<>();

        UserId userId = usersApi.findIdByKeycloakId(callerKeycloakId);
        participantIds.add(userId);

        for (UserId participantId : participants) {
            if (usersApi.existsById(participantId.id())) {
                participantIds.add(participantId);
            }
        }

        if (participantIds.size() < 2) {
            throw new IllegalStateException("Not enough participants");
        }

        Chat chat = new Chat(participants);

        chatRepository.save(chat);

        return chat;
    }
}
