package be.kdg.ipj3.users.shared;

import java.util.Set;
import java.util.UUID;

public record UserCosmeticsDto(
        Set<UUID> unlockedPerkIds,
        UUID equippedBorderId,
        UUID equippedThemeId,
        UUID equippedTitleId
) {}