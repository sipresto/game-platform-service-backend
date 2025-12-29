package be.kdg.ipj3.perks.application;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.shared.PerkId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;

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
}
