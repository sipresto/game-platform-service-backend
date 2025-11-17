package be.kdg.ipj3.shared.domain.game;

import be.kdg.ipj3.shared.domain.Money;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class Game {
    @Identity
    private final GameId gameId;
    private GameName gameName;
    private Money money;
    private GameDescription description;
    private final List<Achievement> achievements = new ArrayList<>();

    public Game(GameName gameName, Money money, GameDescription description) {
        gameId = new GameId(UUID.randomUUID());
        this.gameName = gameName;
        this.money = money;
        this.description = description;
    }

    //only for jpa
    public Game(GameId gameId, GameName gameName, Money money, GameDescription description, List<Achievement> achievements) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.money = money;
        this.description = description;
    }

    public GameId gameId() {
        return gameId;
    }

    public GameName gameName() {
        return gameName;
    }

    public Money price() {
        return money;
    }

    public GameDescription description() {
        return description;
    }

    public List<Achievement> achievements() {
        return achievements;
    }

    public void updateGameName(GameName gameName) {
        this.gameName = gameName;
    }

    public void setANewPrice(BigDecimal price) {
        this.money = new Money(money.currency(), price);
    }

    public void updateDescription(GameDescription description) {
        this.description = description;
    }

    public void addToPrice(BigDecimal price) {
        this.money.add(new Money(money.currency(), price));
    }
}
