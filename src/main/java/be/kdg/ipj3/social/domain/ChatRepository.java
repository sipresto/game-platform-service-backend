package be.kdg.ipj3.social.domain;

import be.kdg.ipj3.users.shared.UserId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepository {
    Optional<Chat> findById(ChatId id);

    List<Chat> findChatsByUserId(UserId id);

    void save(Chat chat);

    void deleteAll();

    Optional<Chat> findByExactParticipants(Set<UserId> participants);
}
