package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public record AchievementProgress(@Identity UserId player, @Identity AchievementId achievement, int amountNeeded,
                                  int amountGotten) {

    @Override
    public UserId player() {
        return player;
    }

    @Override
    public AchievementId achievement() {
        return achievement;
    }
}
