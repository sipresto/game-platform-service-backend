package be.kdg.ipj3.notifications.shared;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum NotificationType {
    FRIEND_REQUEST(true, true) {
        @Override
        public boolean isDuplicate(Notification a, Notification b) {
            return a.getDescription().equals(b.getDescription());
        }
    },
    ACHIEVEMENT(true, false) {
        @Override
        public boolean isDuplicate(Notification a, Notification b) {
            return a.getTitle().equals(b.getTitle());
        }
    },
    CHAT_MESSAGE(true, false),
    YOUR_TURN(true, false) {
        @Override
        public boolean isDuplicate(Notification a, Notification b) {
            return a.getType() == b.getType();
        }
    };

    private final boolean inAppDefault;
    private final boolean emailDefault;

    NotificationType(boolean inAppDefault, boolean emailDefault) {
        this.inAppDefault = inAppDefault;
        this.emailDefault = emailDefault;
    }

    public boolean defaultFor(NotificationChannel channel) {
        return switch (channel) {
            case IN_APP -> inAppDefault;
            case EMAIL -> emailDefault;
        };
    }

    public boolean isDuplicate(Notification a, Notification b) {
        return false;
    }
}
