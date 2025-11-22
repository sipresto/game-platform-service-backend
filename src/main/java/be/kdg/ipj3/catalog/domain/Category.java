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

    public static Category find(String description) {
        for (Category c : Category.values()) {
            if (c.description.equalsIgnoreCase(description)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return description;
    }
}
