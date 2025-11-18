package be.kdg.ipj3.social.domain.message;

import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@Entity
public class Message {
    @Identity
    private final MessageId id;
    private final String message;
    private final Instant sentAt;
    private final UserId sender;

    public Message(MessageId id, String message, UserId sender, Instant sentAt) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.sentAt = sentAt;
    }

    public Message(String message, UserId sender) {
        this(MessageId.create(), message, sender, Instant.now());
    }

    public MessageId getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public UserId getSender() {
        return sender;
    }
}
