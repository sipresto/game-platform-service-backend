package be.kdg.ipj3.perks.api.dto;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkType;

import java.util.UUID;

public record PerkDto(
        UUID id,
        PerkType type,
        String name,
        String description,
        int pointCost
) {
    public static PerkDto fromDomain(Perk perk) {
        return new PerkDto(
                perk.getId().id(),
                perk.getType(),
                perk.getName(),
                perk.getDescription(),
                perk.getPointCost()
        );
    }
}