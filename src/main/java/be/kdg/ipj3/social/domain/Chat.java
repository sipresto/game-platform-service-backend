package be.kdg.ipj3.social.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AggregateRoot
public class Chat {
    @Identity
    private final ChatId id;
    private final Set<UserId> participants = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();
    private Instant lastMessageAt;

    public Chat(ChatId id, Instant lastMessageAt) {
        this.id = id;
        this.lastMessageAt = lastMessageAt;
    }

    public Chat(Set<UserId> participants) {
        this.id = ChatId.create();
        participants.forEach(this::addParticipant);
    }

    public ChatId getId() {
        return id;
    }

    public Set<UserId> getParticipants() {
        return Set.copyOf(participants);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void addParticipant(UserId userId) {
        participants.add(userId);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void sendMessage(UserId senderId, String content) {
        if (!participants.contains(senderId)) {
            throw new AccessDeniedException("User is not a participant of this chat");
        }

        lastMessageAt = Instant.now();
        Message message = new Message(senderId, content, lastMessageAt);
        messages.add(message);
    }
}
