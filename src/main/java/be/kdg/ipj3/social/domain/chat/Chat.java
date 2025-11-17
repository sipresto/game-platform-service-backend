package be.kdg.ipj3.social.domain.chat;

import be.kdg.ipj3.shared.domain.user.UserId;
import be.kdg.ipj3.social.domain.message.Message;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Chat {
    @Identity
    private final ChatId chatId;
    private final UserId[] userIds = new UserId[2];
    private final List<Message> messages = new ArrayList<>();

    public Chat(UserId user1, UserId user2) {
        userIds[0] = user1;
        userIds[1] = user2;
        chatId = new ChatId(UUID.randomUUID());
    }

    public ChatId chatId() {
        return chatId;
    }

    public UserId[] userIds() {
        return userIds;
    }

    public List<Message> messages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
