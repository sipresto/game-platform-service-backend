package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.perks.shared.PerkId;
import org.jmolecules.ddd.annotation.ValueObject;
import java.util.HashSet;
import java.util.Set;

@ValueObject
public class UserCosmetics {
    private final Set<PerkId> unlockedPerkIds;
    private PerkId equippedBorderId;
    private PerkId equippedThemeId;
    private PerkId equippedTitleId;

    public UserCosmetics() {
        this.unlockedPerkIds = new HashSet<>();
    }

    public void unlockPerk(PerkId perkId) {
        unlockedPerkIds.add(perkId);
    }

    public boolean hasPerk(PerkId perkId) {
        return unlockedPerkIds.contains(perkId);
    }

    public void equipBorder(PerkId perkId) {
        if (!unlockedPerkIds.contains(perkId)) {
            throw new IllegalStateException("Cannot equip perk that is not unlocked");
        }
        this.equippedBorderId = perkId;
    }

    public void equipTheme(PerkId perkId) {
        if (!unlockedPerkIds.contains(perkId)) {
            throw new IllegalStateException("Cannot equip perk that is not unlocked");
        }
        this.equippedThemeId = perkId;
    }

    public void equipTitle(PerkId perkId) {
        if (!unlockedPerkIds.contains(perkId)) {
            throw new IllegalStateException("Cannot equip perk that is not unlocked");
        }
        this.equippedTitleId = perkId;
    }

    public PerkId getEquippedBorderId() {
        return equippedBorderId;
    }

    public PerkId getEquippedThemeId() {
        return equippedThemeId;
    }

    public PerkId getEquippedTitleId() {
        return equippedTitleId;
    }
}