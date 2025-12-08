package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.api.dto.GameDto;
import be.kdg.ipj3.catalog.api.dto.GameFilterRequestDto;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.shared.LibraryGameDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
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
        var dtos = gameService.filterGames(filter.minPrice(), filter.maxPrice(), filter.categories()).stream()
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

    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<List<LibraryGameDto>> getOwnedGamesByKeycloakId(
            @PathVariable String keycloakId
    ) {
        List<LibraryGameDto> dtos = gameService.findOwnedGamesByKeycloakId(keycloakId).stream()
                .map(LibraryGameDto::from)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
