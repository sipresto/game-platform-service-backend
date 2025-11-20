package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.GameJpaEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.GameJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbGameRepository implements GameRepository {
    private final GameJpaRepository gameJpaRepository;

    public DbGameRepository(GameJpaRepository gameJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
    }

    @Override
    public void save(Game game) {
        gameJpaRepository.save(GameJpaEntity.fromDomain(game));
    }

    @Override
    public List<Game> getAllGames() {
        return gameJpaRepository.findAll()
                .stream().map(GameJpaEntity::toDomain).toList();
    }
}
