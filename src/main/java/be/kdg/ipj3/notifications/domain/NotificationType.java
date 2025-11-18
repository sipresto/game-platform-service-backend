package be.kdg.ipj3.notifications.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum NotificationType {
    FRIEND_REQUEST,
    ACHIEVEMENT,
    YOUR_TURN
}
