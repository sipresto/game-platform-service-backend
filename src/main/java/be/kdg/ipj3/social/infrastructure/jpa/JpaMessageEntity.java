package be.kdg.ipj3.social.infrastructure.jpa;

import be.kdg.ipj3.social.domain.Message;
import be.kdg.ipj3.social.domain.MessageId;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class JpaMessageEntity {
    @Id
    @Column(name = "message_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private JpaChatEntity chat;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Instant sentAt;

    protected JpaMessageEntity() {
    } // for JPA

    public JpaMessageEntity(UUID id, UUID senderId, String content, Instant sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public static JpaMessageEntity fromDomain(Message message, JpaChatEntity chat) {
        JpaMessageEntity jpaMessage = new JpaMessageEntity(
                message.getId().id(),
                message.getSender().id(),
                message.getContent(),
                message.getSentAt()
        );
        jpaMessage.setChat(chat);
        return jpaMessage;
    }

    public Message toDomain() {
        return new Message(
                new MessageId(id),
                new UserId(senderId),
                content,
                sentAt
        );
    }

    public UUID getId() {
        return id;
    }

    public JpaChatEntity getChat() {
        return chat;
    }

    public void setChat(JpaChatEntity chat) {
        this.chat = chat;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
