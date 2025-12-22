package be.kdg.ipj3.social.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@Entity
public class Message {
    @Identity
    private final MessageId id;
    private final UserId sender;
    private final String content;
    private final Instant sentAt;

    public Message(MessageId id, UserId sender, String content, Instant sentAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Message(UserId sender, String content, Instant sentAt) {
        this(MessageId.create(), sender, content, sentAt);
    }

    public MessageId getId() {
        return id;
    }

    public UserId getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
