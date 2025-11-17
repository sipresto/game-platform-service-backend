package be.kdg.ipj3.social.domain.message;

import be.kdg.ipj3.shared.domain.user.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Message {
    @Identity
    private final MessageId messageId;
    private final String message;
    private final Instant sendAt;
    private final UserId sentById;

    public Message(String message, UserId sentById) {
        this.message = message;
        this.sentById = sentById;
        sendAt = Instant.now();
        messageId = new MessageId(UUID.randomUUID());
    }

    public MessageId messageId() {
        return messageId;
    }

    public String message() {
        return message;
    }

    public Instant sendAt() {
        return sendAt;
    }

    public UserId sentById() {
        return sentById;
    }
}
