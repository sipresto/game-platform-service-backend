package be.kdg.ipj3.perks.domain;

import be.kdg.ipj3.perks.shared.PerkId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class Perk {
    @Identity
    private final PerkId id;
    private final PerkType type;
    private final String name;
    private final String description;
    private final int pointCost;

    public Perk(PerkId id, PerkType type, String name, String description, int pointCost) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.pointCost = pointCost;
    }

    public PerkId getId() {
        return id;
    }

    public PerkType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPointCost() {
        return pointCost;
    }
}
