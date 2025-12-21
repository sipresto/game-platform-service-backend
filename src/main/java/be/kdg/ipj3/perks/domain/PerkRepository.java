package be.kdg.ipj3.perks.domain;

import be.kdg.ipj3.perks.shared.PerkId;

import java.util.List;
import java.util.Optional;

public interface PerkRepository {

    Optional<Perk> findById(PerkId id);

    List<Perk> findAllPerks();

    void deleteAll();

    void save(Perk perk);
}
