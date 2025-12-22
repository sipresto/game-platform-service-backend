package be.kdg.ipj3.social.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

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

    public Chat(ChatId id) {
        this.id = id;
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

    public void addParticipant(UserId userId) {
        participants.add(userId);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void sendMessage(UserId senderId, String content) {
        Message message = new Message(senderId, content);
        messages.add(message);
    }
}
