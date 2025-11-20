package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/games")
public class GameRestController {
    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames() {
        List<GameDto> dtos = gameService.getGames().stream().map(GameDto::from).toList();

        return ResponseEntity.ok(dtos);
    }
}
