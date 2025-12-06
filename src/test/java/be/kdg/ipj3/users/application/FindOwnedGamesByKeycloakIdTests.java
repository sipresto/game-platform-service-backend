package be.kdg.ipj3.users.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FindOwnedGamesByKeycloakIdTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    // These tests will fail since findOwnedGamesByKeycloakId now returns a list of games and not gameIds

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnGameIds() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();
        UserId userId = new UserId(UUID.fromString(keycloakId));
        GameId gameId = GameId.create();
        List<Game> game = List.of(new Game(
                gameId,
                "game 1",
                "description",
                Money.of(BigDecimal.valueOf(19.99)),
                "https://test.com/service",
                "A rule",
                List.of(),
                List.of(Category.NATIVE)
        ));
        List<GameId> expectedGameIds = List.of(
                gameId
        );
        given(userRepository.findOwnedGamesById(userId)).willReturn(expectedGameIds);

        // Act
        List<Game> result = userService.findOwnedGamesByKeycloakId(keycloakId);

        // Assert
        assertThat(result).isEqualTo(game);
        then(userRepository).should().findOwnedGamesById(userId);
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnEmptyList_whenUserHasNoGames() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        UserId userId = UserId.create();

        given(userRepository.findOwnedGamesById(userId)).willReturn(List.of());

        // Act
        List<Game> result = userService.findOwnedGamesByKeycloakId(keycloakId);

        // Assert
        assertThat(result).isEmpty();
        then(userRepository).should().findOwnedGamesById(userId);
    }
}
