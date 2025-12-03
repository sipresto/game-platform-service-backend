package be.kdg.ipj3.catalog.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class GameType {
    private final String code;

    public static final GameType TIC_TAC_TOE = new GameType("TIC_TAC_TOE");
    public static final GameType QUORIDOR = new GameType("QUORIDOR");

    public GameType(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Game type code cannot be null or empty");
        }
        this.code = code.toUpperCase();
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameType gameType = (GameType) o;
        return code.equals(gameType.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
