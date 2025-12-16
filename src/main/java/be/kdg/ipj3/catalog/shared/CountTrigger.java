package be.kdg.ipj3.catalog.shared;

public enum CountTrigger {
    WIN_PLAYER,
    WIN_AI,
    WIN_ANY,
    DRAW_PLAYER,
    DRAW_AI,
    DRAW_ANY,
    WIN_OR_DRAW_ANY,
    PLAY_ANY;

    public static CountTrigger fromStringOrNull(String value) {
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
