package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto.RegisterGameDto;
import be.kdg.ipj3.catalog.shared.*;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public List<Game> findAllGames() {
        return gameRepository.findAllGames();
    }

    @Transactional(readOnly = true)
    public String findBackendUrlByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findBackendUrlByGameId(gameId)
                .orElseThrow(gameId::notFound);
    }

    @Transactional(readOnly = true)
    public String findFrontendUrlByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findFrontendUrlByGameId(gameId)
                .orElseThrow(gameId::notFound);
    }

    @Transactional(readOnly = true)
    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories) {
        return gameRepository.filterGames(minPrice, maxPrice, categories);
    }

    @Transactional(readOnly = true)
    public Game findGameById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.existsById(gameId);
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> findAchievementDtosByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findAchievementsByGameId(gameId).stream()
                .map(AchievementDto::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameAchievementDto> findGameAchievementDtosByIdWithAchievements(UUID id) {
        var gameId = new GameId(id);
        var game = gameRepository.findByIdWithAchievements(gameId);
        if (game.isEmpty()) {
            return List.of();
        }
        List<GameAchievementDto> gameAchievementDtos = new ArrayList<>();
        for (Achievement achievement : game.get().getAchievements()) {
            gameAchievementDtos.add(new GameAchievementDto(
                    gameId,
                    game.get().getName(),
                    achievement.getId(),
                    achievement.getName(),
                    achievement.getDescription(),
                    achievement.getRequiredValue()
            ));
        }
        return gameAchievementDtos;
    }

    @Transactional(readOnly = true)
    public Optional<AchievementDto> findAchievementDtoByGameIdAndCode(GameId gameId, String code) {
        return gameRepository.findAchievementByGameIdAndCode(gameId, code)
                .map(AchievementDto::fromDomain);
    }

    @Transactional(readOnly = true)
    public Optional<GamePurchaseDto> findGameDtoById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findById(gameId)
                .map(GamePurchaseDto::fromDomain);
    }

    @Transactional(readOnly = true)
    public List<Game> findOwnedGamesByKeycloakId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId);
        List<GameId> ownedGameIds = usersApi.findOwnedGamesById(userId.id());
        return gameRepository.findGamesByIds(ownedGameIds);
    }

    @Transactional(readOnly = true)
    public List<Game> findFavoriteGamesByKeycloakId(String keycloakId) {
        var userId = usersApi.findIdByKeycloakId(keycloakId);
        var favoriteGamesIds = usersApi.findFavoriteGamesByUserId(userId.id());
        return gameRepository.findGamesByIds(favoriteGamesIds);
    }

    public void favoriteGame(String id,UUID gameId){
        if(!existsById(gameId)){
            throw GameId.notFound(gameId);
        }
        usersApi.addFavorite(id, gameId);
    }

    public void unfavoriteGame(String id,UUID gameId){
        if(!existsById(gameId)){
            throw GameId.notFound(gameId);
        }
        usersApi.removeFavorite(id, gameId);
    }

    public void registerGame(RegisterGameDto dto) {
        Game game = gameRepository.findById(new GameId(dto.id()))
                .orElse(null);

        if (game != null) {
            game.activate(dto.timestamp());
            gameRepository.save(game);
        }
        else {
            game = dto.toDomain();
            gameRepository.save(game);
        }
    }
}