package be.kdg.ipj3.sessions.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.sessions.api.dto.GameStartDto;
import be.kdg.ipj3.sessions.api.dto.GameUrlDto;
import be.kdg.ipj3.sessions.application.LobbyService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {
    private final LobbyService lobbyService;
    private final GameService gameService;

    public LobbyController(LobbyService lobbyService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameUrlDto> startGame(
            @Valid @RequestBody GameStartDto dto
    ) {
        String serviceUrl = gameService.findServiceByGameId(new GameId(dto.gameId()));

        List<UserId> playerIds = dto.players()
                .stream()
                .map(UserId::new)
                .toList();

        String gameUrl = lobbyService.startGame(serviceUrl, playerIds, dto.difficulty());

        return ResponseEntity.ok(new GameUrlDto(gameUrl));
    }
}
