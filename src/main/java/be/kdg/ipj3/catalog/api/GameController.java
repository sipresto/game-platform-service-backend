package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.api.dto.GameDto;
import be.kdg.ipj3.catalog.api.dto.GameFilterRequestDto;
import be.kdg.ipj3.catalog.api.dto.LibraryGameDto;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/platform/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        List<GameDto> dtos = gameService.findAllGames().stream()
                .map(GameDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<GameDto>> getAllGamesByFilter(
            @RequestBody GameFilterRequestDto filter
    ) {
        Money minPrice = filter.minPrice() != null ? Money.of(filter.minPrice()) : null;
        Money maxPrice = filter.maxPrice() != null ? Money.of(filter.maxPrice()) : null;
        var dtos = gameService.filterGames(minPrice, maxPrice, filter.categories()).stream()
                .map(GameDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameById(
            @PathVariable UUID id
    ) {
        GameDto gameDto = GameDto.fromDomain(gameService.findGameById(id));

        return ResponseEntity.ok(gameDto);
    }

    @PatchMapping("/library/favorite/{gameId}")
    public ResponseEntity<Void> favoriteGame(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gameId
    ) {
        String keycloakId = jwt.getSubject();

        gameService.favoriteGame(keycloakId, gameId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/library/unfavorite/{gameId}")
    public ResponseEntity<Void> unfavoriteGame(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gameId
    ) {
        String keycloakId = jwt.getSubject();

        gameService.unfavoriteGame(keycloakId, gameId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<List<LibraryGameDto>> getOwnedGamesByKeycloakId(
            @PathVariable String keycloakId
    ) {
        Set<GameId> favoriteIds = gameService
                .findFavoriteGamesByKeycloakId(keycloakId)
                .stream()
                .map(Game::getId)
                .collect(Collectors.toSet());

        List<LibraryGameDto> dtos = gameService.findOwnedGamesByKeycloakId(keycloakId).stream()
                .map(game -> new LibraryGameDto(
                        game.getId().id(),
                        game.getName(),
                        game.getDescription(),
                        game.getCategories(),
                        favoriteIds.contains(game.getId())
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
