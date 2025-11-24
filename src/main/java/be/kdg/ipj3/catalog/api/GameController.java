package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        List<GameDto> dtos = gameService.getAllGames().stream().map(GameDto::from).toList();
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<GameDto>> getAllGamesByFilter(@RequestBody GameFilterRequest filter) {
        var dtos = gameService.filterGames(filter.minPrice(), filter.maxPrice(), filter.categories()).stream().map(GameDto::from).toList();
        if(dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }
        return ResponseEntity.ok(dtos);
    }
}
