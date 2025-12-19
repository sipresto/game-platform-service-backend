package be.kdg.ipj3.perks.application;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.shared.PerkId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PerkService {

    private final PerkRepository perkRepository;

    public PerkService(PerkRepository perkRepository) {
        this.perkRepository = perkRepository;
    }

    public List<Perk> findAllPerks() {
        return perkRepository.findAllPerks().orElseThrow();
    }

    public Perk findPerkById(UUID id){
        var perkId = new PerkId(id);
        return perkRepository.findById(perkId).orElseThrow(perkId::notFound);
    }
}
