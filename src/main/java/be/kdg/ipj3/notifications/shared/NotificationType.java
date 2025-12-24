package be.kdg.ipj3.notifications.shared;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum NotificationType {
    FRIEND_REQUEST,
    ACHIEVEMENT,
    YOUR_TURN,
    CHAT_MESSAGE
}
