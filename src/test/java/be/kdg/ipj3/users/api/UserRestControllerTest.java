package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.util.List.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GameService gameService;

    private UserRestController userRestController;

    private UserId userId;
    private User user;
    private GameId game1Id;
    private Game game1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRestController = new UserRestController(userService, gameService);
        userId = new UserId(UUID.randomUUID().toString());
        game1Id = GameId.create();

        user = new User(userId, "john", of(game1Id));

        game1 = new Game(
                game1Id,
                "Test Game",
                "Desc",
                Money.of(BigDecimal.valueOf(9.99)),
                of(),
                of(Category.BOARDGAME)
        );
    }

    @Test
    void testGetUserWithGames_WithOwnedGames() {
        when(userService.getUserWithGames(userId)).thenReturn(user);
        when(gameService.getGameById(game1Id)).thenReturn(game1);

        ResponseEntity<UserDto> response =
                userRestController.getUserWithGames(UUID.fromString(userId.value()));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("john", dto.username());
        assertEquals(1, dto.gameDto().size());
    }

    @Test
    void testGetUserWithGames_NoGames() {
        User userNoGames = new User(userId, "john", of());

        when(userService.getUserWithGames(userId)).thenReturn(userNoGames);

        ResponseEntity<UserDto> response =
                userRestController.getUserWithGames(UUID.fromString(userId.value()));

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().gameDto().size());
    }

    @Test
    void testGetUserWithGames_UserNotFound() {
        when(userService.getUserWithGames(userId))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> userRestController.getUserWithGames(UUID.fromString(userId.value()))
        );
    }
}
