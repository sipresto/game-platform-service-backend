package be.kdg.ipj3.shared.domain.profile;

import be.kdg.ipj3.shared.domain.game.Achievement;
import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.user.UserId;
import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Objects;

@Entity
public class AchievementProgress {
    @Identity
    @NotNull
    private final UserId ownerId;
    @Identity
    @NotNull
    private final GameId gameId;
    @NotNull
    private final int amountNeeded;
    @NotNull
    private int amountGotten;
    @NotNull
    private final Achievement achievement;

    public AchievementProgress(@NotNull UserId ownerId, @NotNull GameId gameId, @NotNull int amountNeeded, @NotNull int amountGotten, Achievement achievement) {
        this.achievement = achievement;
        Objects.requireNonNull(ownerId, "ownerId cannot be null!");
        Objects.requireNonNull(gameId, "gameId cannot be null!");
        if (amountNeeded <= 0 || amountGotten < 0 || amountGotten > amountNeeded) {
            throw new IllegalArgumentException("amountNeeded needs to be larger than 0 and amountGotten cannot be larger than amountNeeded and amountGotten cannot be smaller than 0!");
        }
        this.ownerId = ownerId;
        this.gameId = gameId;
        this.amountNeeded = amountNeeded;
        this.amountGotten = amountGotten;
    }

    public UserId ownerId() {
        return ownerId;
    }

    public GameId gameId() {
        return gameId;
    }

    public Achievement achievement() {
        return achievement;
    }

    public int amountNeeded() {
        return amountNeeded;
    }

    public int amountGotten() {
        return amountGotten;
    }

    public int getPercentageProgress() {
        return (amountGotten * 100) / amountNeeded;
    }

    public void addProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("Progress to add cannot be negative.");
        }
        int newAmountGotten = this.amountGotten + progress;
        if (newAmountGotten > amountNeeded) {
            this.amountGotten = amountNeeded;
        } else {
            this.amountGotten = newAmountGotten;
        }
    }
}
