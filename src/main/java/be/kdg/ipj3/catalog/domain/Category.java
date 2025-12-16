package be.kdg.ipj3.catalog.domain;

public enum Category {
    STRATEGY("strategie"),
    BOARDGAME("boardgame"),
    CARDGAME("cardgame"),
    NATIVE("native"),
    EXTERNAL("extern");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public static Category fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
