package be.kdg.ipj3.social.domain.chat;

import be.kdg.ipj3.social.domain.message.Message;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Chat {
    @Identity
    private final ChatId id;
    private final Set<UserId> participants = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public Chat(ChatId id, Set<UserId> participants, List<Message> messages) {
        this.id = id;
        this.participants.addAll(participants);
        this.messages.addAll(messages);
    }

    public Chat(Set<UserId> participants) {
        this(ChatId.create(), participants, List.of());
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

    public void addMessage(Message message) {
        messages.add(message);
    }
}
