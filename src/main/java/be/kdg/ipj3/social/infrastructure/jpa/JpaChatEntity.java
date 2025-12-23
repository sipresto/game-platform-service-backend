package be.kdg.ipj3.social.infrastructure.jpa;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatId;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "chats")
public class JpaChatEntity {
    @Id
    @Column(name = "chat_id")
    private UUID id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id")
    )
    @Column(name = "user_id")
    private Set<UUID> participantIds = new HashSet<>();

    @OneToMany(
            mappedBy = "chat",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sentAt ASC")
    private List<JpaMessageEntity> messages = new ArrayList<>();

    private Instant lastMessageAt;

    protected JpaChatEntity() {
    } // for JPA

    public JpaChatEntity(UUID id, String name, Set<UUID> participantIds, Instant lastMessageAt) {
        this.id = id;
        this.name = name;
        this.participantIds = participantIds;
        this.lastMessageAt = lastMessageAt;
    }

    public static JpaChatEntity fromDomain(Chat chat) {
        Set<UUID> participantIds = chat.getParticipants().stream()
                .map(UserId::id)
                .collect(Collectors.toSet());

        JpaChatEntity jpaChat = new JpaChatEntity(chat.getId().id(), chat.getName(), participantIds, chat.getLastMessageAt());

        chat.getMessages().stream()
                .map(msg -> JpaMessageEntity.fromDomain(msg, jpaChat))
                .forEach(jpaChat::addMessage);

        return jpaChat;
    }

    public Chat toDomain() {
        Chat chat = new Chat(new ChatId(id), name, lastMessageAt);

        participantIds.stream()
                .map(UserId::new)
                .forEach(chat::addParticipant);

        messages.stream()
                .map(JpaMessageEntity::toDomain)
                .forEach(chat::addMessage);

        return chat;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getParticipantIds() {
        return Set.copyOf(participantIds);
    }

    public List<JpaMessageEntity> getMessages() {
        return List.copyOf(messages);
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void addMessage(JpaMessageEntity message) {
        messages.add(message);
        message.setChat(this);
    }
}
