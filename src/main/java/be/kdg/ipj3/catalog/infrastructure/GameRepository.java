package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GameRepository implements IGameRepository {
    private final JpaGameRepository jpaGameRepository;

    public GameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        jpaGameRepository.save(JpaGameEntity.fromDomain(game));
    }

    @Override
    public List<Game> findAllGames() {
        return jpaGameRepository.findAll()
                .stream().map(JpaGameEntity::toDomain).toList();
    }

    @Override
    public Optional<String> findServiceUrlByGameId(GameId id) {
        return jpaGameRepository.findById(id.id())
                .map(JpaGameEntity::getServiceUrl);
    }
}
