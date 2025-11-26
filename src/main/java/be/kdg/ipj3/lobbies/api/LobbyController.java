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

    private UserId resolveUserId(Jwt token) {
        String keycloakId = token.getSubject();
        String keycloakUsername = token.getClaim("preferred_username");

        User user = userService.registerOrGetUser(keycloakId, keycloakUsername);

        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<LobbyListDto>> getLobbies() {
        List<Lobby> lobbies = lobbyService.getAllOpenLobbies();

        List<LobbyListDto> dtos = lobbies.stream()
                .map(l -> new LobbyListDto(
                        l.getId().id(),
                        l.getPlayers().size(),
                        l.isPrivate()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<LobbyCreatedResponseDto> createLobby(
            @RequestBody CreateLobbyDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId hostId = resolveUserId(token);

        Lobby createdLobby = lobbyService.createLobby(
                dto.password(),
                dto.maxPlayers(),
                new GameId(dto.gameId()),
                hostId
        );
        var lobbyId = new LobbyCreatedResponseDto(createdLobby.getId().id());

        return ResponseEntity.ok(lobbyId);
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

    @PostMapping("/{id}/start")
    public ResponseEntity<GameUrlDto> startGame(
            @PathVariable String id,
            @RequestBody GameStartDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        UserId hostId = resolveUserId(token);
        String serviceUrl = gameService.findServiceByGameId(new GameId(dto.gameId()));

        String gameUrl = lobbyService.startGame(serviceUrl, id, hostId, dto.difficulty());
        return ResponseEntity.ok(new GameUrlDto(gameUrl));
    }

//    @PostMapping
//    public ResponseEntity<GameUrlDto> startGame(
//            @Valid @RequestBody GameStartDto dto
//    ) {
//        String serviceUrl = gameService.findServiceByGameId(new GameId(dto.gameId()));
//
//        List<UserId> playerIds = dto.players()
//                .stream()
//                .map(UserId::new)
//                .toList();
//
//        String gameUrl = lobbyService.startGame(serviceUrl, playerIds, dto.difficulty());
//
//        return ResponseEntity.ok(new GameUrlDto(gameUrl));
//    }
}
