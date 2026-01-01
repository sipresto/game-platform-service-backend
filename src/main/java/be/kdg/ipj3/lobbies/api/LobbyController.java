package be.kdg.ipj3.lobbies.api;

import be.kdg.ipj3.lobbies.api.dto.*;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.Lobby;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/lobbies")
public class LobbyController {
    private final LobbyService lobbyService;


    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;

    }

    @GetMapping
    public ResponseEntity<List<LobbyListDto>> getAllOpenLobbies(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<Lobby> lobbies = lobbyService.findAllOpenLobbies(jwt.getSubject());

        List<LobbyListDto> dtos = lobbies.stream()
                .map(LobbyListDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<LobbyCreatedResponseDto> createLobby(
            @Valid @RequestBody CreateLobbyDto dto,
            @AuthenticationPrincipal Jwt token
    ) {

        String keycloakId = token.getSubject();

        Lobby createdLobby = lobbyService.createLobby(
                keycloakId,
                dto.password(),
                dto.maxPlayers(),
                dto.gameId()
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
        String keycloakId = token.getSubject();

        lobbyService.joinLobby(id, keycloakId, dto.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveLobby(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        String keycloakId = token.getSubject();

        lobbyService.leaveLobby(id, keycloakId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<GameUrlDto> startGame(
            @PathVariable UUID id,
            @RequestBody GameStartDto dto,
            @AuthenticationPrincipal Jwt token
    ) {
        String keycloakId = token.getSubject();

        String gameUrl = lobbyService.startGame(id, keycloakId, dto.difficulty());

        return ResponseEntity.ok(new GameUrlDto(gameUrl));
    }


    @GetMapping("game/{gameId}")
    public ResponseEntity<List<LobbyListDto>> getOpenLobbiesForGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        var openLobbies = lobbyService.findOpenLobbiesForGame(gameId, jwt.getSubject());

        var dto = openLobbies.stream()
                .map(LobbyListDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyDetailDto> getLobby(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        LobbyDetailDto lobbyDetail = lobbyService.getLobbyDetails(id, jwt.getSubject());

        return ResponseEntity.ok(lobbyDetail);
    }
}