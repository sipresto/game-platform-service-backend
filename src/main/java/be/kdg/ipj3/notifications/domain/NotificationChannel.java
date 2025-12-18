package be.kdg.ipj3.notifications.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum NotificationChannel {
    IN_APP,
    EMAIL
}