package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.GameJpaEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Game> getGameById(GameId gameId) {
        return jpaGameRepository.findById(gameId.id()).map(GameJpaEntity::toDomain);
    }
}
