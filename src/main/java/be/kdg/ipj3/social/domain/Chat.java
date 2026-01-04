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
    private final String name;
    private final Set<UserId> participants = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();
    private Instant lastMessageAt;
    private boolean blocked;

    public Chat(ChatId id, String name, Instant lastMessageAt, boolean blocked) {
        this.id = id;
        this.name = name;
        this.lastMessageAt = lastMessageAt;
        this.blocked = blocked;
    }

    public Chat(String name, Set<UserId> participants) {
        this.id = ChatId.create();
        this.name = name;
        participants.forEach(this::addParticipant);
        this.blocked = false;
    }

    public ChatId getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean isBlocked() {
        return blocked;
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

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }
}
