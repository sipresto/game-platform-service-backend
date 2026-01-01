package be.kdg.ipj3.perks.api.dto;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkType;

import java.util.UUID;

public record UserPerkDto(
        UUID perkId,
        String name,
        String description,
        PerkType type,
        int pointCost,
        boolean purchased,
        boolean equipped
) {
    public static UserPerkDto fromDomain(Perk perk, boolean purchased, boolean equipped) {
        return new UserPerkDto(
                perk.getId().id(),
                perk.getName(),
                perk.getDescription(),
                perk.getType(),
                perk.getPointCost(),
                purchased,
                equipped
        );
    }
}