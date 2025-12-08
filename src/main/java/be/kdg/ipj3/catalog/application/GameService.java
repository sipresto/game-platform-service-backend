package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.AchievementDto;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamePurchaseDto;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GameService implements GamesApi {
    private final GameRepository gameRepository;
    private final UsersApi usersApi;

    public GameService(GameRepository gameRepository, UsersApi usersApi) {
        this.gameRepository = gameRepository;
        this.usersApi = usersApi;
    }

    public List<Game> findAllGames() {
        return gameRepository.findAllGames();
    }

    public String findServiceByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findServiceUrlByGameId(gameId)
                .orElseThrow(gameId::notFound);
    }

    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories) {
        return gameRepository.filterGames(minPrice, maxPrice, categories);
    }

    public Game findGameById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    public boolean existsById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.existsById(gameId);
    }

    public List<AchievementDto> findAchievementDtosByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findAchievementsByGameId(gameId).stream()
                .map(AchievementDto::fromDomain)
                .toList();
    }

    public Optional<AchievementDto> findAchievementDtoByCode(String code) {
        return gameRepository.findAchievementByCode(code)
                .map(AchievementDto::fromDomain);
    }

    public Optional<GamePurchaseDto> findGameDtoById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findById(gameId)
                .map(GamePurchaseDto::fromDomain);
    }

    public List<Game> findOwnedGamesByKeycloakId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId);
        List<GameId> ownedGameIds = usersApi.findOwnedGamesById(userId.id());
        return gameRepository.findGamesByIds(ownedGameIds);
    }
}