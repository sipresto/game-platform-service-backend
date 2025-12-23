package be.kdg.ipj3.perks.infrastructure;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.infrastructure.jpa.JpaPerkEntity;
import be.kdg.ipj3.perks.infrastructure.jpa.JpaPerkRepository;
import be.kdg.ipj3.perks.shared.PerkId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PerkRepositoryImpl implements PerkRepository {

    private final JpaPerkRepository jpaPerkRepository;

    public PerkRepositoryImpl(JpaPerkRepository jpaPerkRepository) {
        this.jpaPerkRepository = jpaPerkRepository;
    }

    @Override
    public Optional<Perk> findById(PerkId id) {
        return jpaPerkRepository.findById(id.id()).map(JpaPerkEntity::toDomain);
    }

    @Override
    public List<Perk> findAllPerks() {
        return jpaPerkRepository.findAll()
                .stream()
                .map(JpaPerkEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Perk perk) {
        JpaPerkEntity entity = JpaPerkEntity.fromDomain(perk);
        jpaPerkRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        jpaPerkRepository.deleteAll();
    }
}
