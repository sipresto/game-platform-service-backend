package be.kdg.ipj3.perks.application;

import be.kdg.ipj3.perks.api.dto.UserPerkDto;
import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.domain.PerkType;
import be.kdg.ipj3.perks.shared.PerkId;
import be.kdg.ipj3.users.shared.UserCosmeticsDto;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PerkService {

    private final UsersApi usersApi;
    private final PerkRepository perkRepository;

    public PerkService(UsersApi usersApi, PerkRepository perkRepository) {
        this.usersApi = usersApi;
        this.perkRepository = perkRepository;
    }

    public List<Perk> findAllPerks() {
        return perkRepository.findAllPerks();
    }

    public Perk findPerkById(UUID id){
        var perkId = new PerkId(id);
        return perkRepository.findById(perkId).orElseThrow(perkId::notFound);
    }

    public void purchasePerk(UUID perkId, String keycloakId){

        var perk = findPerkById(perkId);
        var userId = usersApi.findIdByKeycloakId(keycloakId);

        usersApi.purchasePerk(perk.getId().id(), perk.getPointCost() ,userId);
    }

    public void equipPerk(UUID perkId, String keycloakId) {

        var perk = findPerkById(perkId);
        var userId = usersApi.findIdByKeycloakId(keycloakId);

        switch (perk.getType()) {
            case BORDER -> usersApi.equipBorder(userId, perk.getId().id());
            case THEME -> usersApi.equipTheme(userId, perk.getId().id());
            case TITLE -> usersApi.equipTitle(userId, perk.getId().id());
        }
    }

    public void unequipPerk(UUID perkId, String keycloakId) {
        var perk = findPerkById(perkId);
        var userId = usersApi.findIdByKeycloakId(keycloakId);

        switch (perk.getType()) {
            case BORDER -> usersApi.unequipBorder(userId);
            case THEME -> usersApi.unequipTheme(userId);
            case TITLE -> usersApi.unequipTitle(userId);
        }
    }

    @Transactional(readOnly = true)
    public List<UserPerkDto> findAllPerksForUser(String keycloakId) {
        var userId = usersApi.findIdByKeycloakId(keycloakId);
        var userCosmetics = usersApi.getUserCosmetics(userId);

        return findAllPerks().stream()
                .map(perk -> {
                    UUID perkId = perk.getId().id();

                    boolean purchased = userCosmetics.unlockedPerkIds() != null &&
                                        userCosmetics.unlockedPerkIds().contains(perkId);

                    boolean equipped = isPerkEquipped(userCosmetics, perkId, perk.getType());

                    return UserPerkDto.fromDomain(perk, purchased, equipped);
                })
                .toList();
    }

    private boolean isPerkEquipped(UserCosmeticsDto cosmeticsDto, UUID perkId, PerkType type) {
        if (perkId == null) return false;

        return switch (type) {
            case BORDER -> perkId.equals(cosmeticsDto.equippedBorderId());
            case THEME -> perkId.equals(cosmeticsDto.equippedThemeId());
            case TITLE -> perkId.equals(cosmeticsDto.equippedTitleId());
        };
    }
}
