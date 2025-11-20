package be.kdg.ipj3.catalog.domain;

public enum Category {
    STRATEGY("strategie"),
    BOARDGAME("boardgame"),
    CARDGAME("cardgame"),
    STUDENT("student"),
    TEACHER("leerkracht");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
