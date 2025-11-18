package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class AchievementProgress {
    @Identity
    private final UserId player;
    @Identity
    private final AchievementId achievement;
    private final int amountNeeded;
    private final int amountGotten;

    public AchievementProgress(UserId player, AchievementId achievement, int amountNeeded, int amountGotten) {
        this.player = player;
        this.achievement = achievement;
        this.amountNeeded = amountNeeded;
        this.amountGotten = amountGotten;
    }

    public UserId getPlayer() {
        return player;
    }

    public AchievementId getAchievement() {
        return achievement;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public int getAmountGotten() {
        return amountGotten;
    }
}
