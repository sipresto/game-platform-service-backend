package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.GameJpaEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbGameRepository implements GameRepository {
    private final JpaGameRepository jpaGameRepository;

    public DbGameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        jpaGameRepository.save(GameJpaEntity.fromDomain(game));
    }

    @Override
    public List<Game> getAllGames() {
        return jpaGameRepository.findAll()
                .stream().map(GameJpaEntity::toDomain).toList();
    }
}
