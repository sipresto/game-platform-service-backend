package be.kdg.ipj3.sessions.shared;

public enum SessionStatus {
    IN_PROGRESS,
    PLAYER_WIN,
    AI_WIN,
    DRAW;

    public static SessionStatus fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
