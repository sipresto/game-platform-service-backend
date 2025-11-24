package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.api.GameDto;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.api.SmallGameDto;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    private final GameService gameService;

    public UserRestController(
            UserService userService,
            GameService gameService
    ) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<UserDto> getUserWithGames(
            @PathVariable UUID id
    ) {
        User user = userService.getUserWithGames(new UserId(id.toString()));
        List<SmallGameDto> games = new ArrayList<>();
        if (!user.getOwnedGames().isEmpty()) {
            user.getOwnedGames().forEach(gameId -> games.add(SmallGameDto.from(gameService.getGameById(gameId))));
        }

        UserDto userDtos = UserDto.from(user, games);
        return ResponseEntity.ok(userDtos);
    }
}
