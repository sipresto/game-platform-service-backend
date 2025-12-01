package be.kdg.ipj3.lobbies.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.lobbies.api.dto.*;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {
    private final LobbyService lobbyService;
    private final GameService gameService;
    private final UserService userService;

    public LobbyController(LobbyService lobbyService, GameService gameService, UserService userService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
        this.userService = userService;
    }

    // TODO: Not auto-register users, but verify if they are registered, using commented code below
    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return user.getId();
    }

    /*private UserId resolveUserId(Jwt token) {
        return userService
                .findByKeycloakId(token.getSubject())
                .map(User::getId)
                .orElseThrow(() -> new UserNotFoundException(token.getSubject()));
    }*/

    @GetMapping
    public ResponseEntity<List<LobbyListDto>> getAllOpenLobbies() {
        List<Lobby> lobbies = lobbyService.findAllOpenLobbies();

        List<LobbyListDto> dtos = lobbies.stream()
                .map(LobbyListDto::from)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<LobbyCreatedResponseDto> createLobby(
            @Valid @RequestBody CreateLobbyDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId hostId = resolveUserId(token);

        if (gameService.existsById(new GameId(dto.gameId()))) {
            Lobby createdLobby = lobbyService.createLobby(
                    dto.password(),
                    dto.maxPlayers(),
                    new GameId(dto.gameId()),
                    hostId
            );
            var lobbyId = new LobbyCreatedResponseDto(createdLobby.getId().id());

            return ResponseEntity.ok(lobbyId);
        }

        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinLobby(
            @PathVariable UUID id,
            @RequestBody JoinLobbyDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId playerId = resolveUserId(token);
        LobbyId lobbyId = new LobbyId(id);

        lobbyService.joinLobby(lobbyId, playerId, dto.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveLobby(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId playerId = resolveUserId(token);
        LobbyId lobbyId = new LobbyId(id);

        lobbyService.leaveLobby(lobbyId, playerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameUrlDto> startGame(
            @PathVariable UUID id,
            @Valid @RequestBody GameStartDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId hostId = resolveUserId(token);
        // TODO: Logic should change when GameStartDto gets reworked
        String serviceUrl = gameService.findServiceByGameId(new GameId(dto.gameId()));

        String gameUrl = lobbyService.startGame(serviceUrl, new LobbyId(id), hostId, dto.difficulty());
        return ResponseEntity.ok(new GameUrlDto(gameUrl));
    }

    // TODO: change path to /game/{gameId}
    @GetMapping("/{gameId}")
    public ResponseEntity<List<LobbyListDto>> getOpenLobbiesForGame(
            @PathVariable UUID gameId
    ) {
        if (gameService.existsById(new GameId(gameId))) {
            var openLobbies = lobbyService.findOpenLobbiesForGame(new GameId(gameId));

            var dtos = openLobbies.stream()
                    .map(LobbyListDto::from)
                    .toList();

            return ResponseEntity.ok(dtos);
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
}
