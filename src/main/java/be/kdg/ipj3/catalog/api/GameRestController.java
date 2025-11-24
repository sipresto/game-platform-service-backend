package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        List<GameDto> dtos = gameService.findAllGames().stream().map(GameDto::from).toList();
        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }
        return ResponseEntity.ok(dtos);
    }
}
