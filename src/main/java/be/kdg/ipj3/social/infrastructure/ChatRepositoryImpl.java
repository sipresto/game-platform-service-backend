package be.kdg.ipj3.social.infrastructure;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.social.infrastructure.jpa.JpaChatEntity;
import be.kdg.ipj3.social.infrastructure.jpa.JpaChatRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepositoryImpl implements ChatRepository {
    private final JpaChatRepository jpaChatRepository;

    public ChatRepositoryImpl(JpaChatRepository jpaChatRepository) {
        this.jpaChatRepository = jpaChatRepository;
    }

    @Override
    public Optional<Chat> findById(ChatId id) {
        return jpaChatRepository.findById(id.id())
                .map(JpaChatEntity::toDomain);
    }

    @Override
    public List<Chat> findChatsByUserId(UserId id) {
        return jpaChatRepository.findByParticipantIdsContaining(id.id()).stream()
                .map(JpaChatEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Chat chat) {
        jpaChatRepository.save(JpaChatEntity.fromDomain(chat));
    }

    @Override
    public void deleteAll() {
        jpaChatRepository.deleteAll();
    }
}
