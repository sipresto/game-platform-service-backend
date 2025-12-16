package be.kdg.ipj3.catalog.shared;

public enum CountingStrategy {
    INCREMENT,
    STREAK,
    CONDITION;

    public static CountingStrategy fromStringOrNull(String value) {
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
