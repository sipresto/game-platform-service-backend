package be.kdg.ipj3.perks.api.dto;

import be.kdg.ipj3.perks.domain.Perk;

import java.util.UUID;

public record PerkDto(
        UUID id,
        String type,
        String name,
        String description,
        int pointCost
) {
    public static PerkDto fromDomain(Perk perk) {
        return new PerkDto(
                perk.getId().id(),
                perk.getType().toString(),
                perk.getName(),
                perk.getDescription(),
                perk.getPointCost()
        );
    }
}