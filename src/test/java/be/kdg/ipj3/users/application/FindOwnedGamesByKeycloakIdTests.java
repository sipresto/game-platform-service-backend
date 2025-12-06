package be.kdg.ipj3.users.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnGameIds() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        List<GameId> expectedGameIds = List.of(
                new GameId(UUID.randomUUID()),
                new GameId(UUID.randomUUID())
        );
        given(userRepository.findOwnedGamesByKeycloakId(keycloakId)).willReturn(expectedGameIds);

        // Act
        List<GameId> result = userService.findOwnedGamesByKeycloakId(keycloakId);

        // Assert
        assertThat(result).isEqualTo(expectedGameIds);
        then(userRepository).should().findOwnedGamesByKeycloakId(keycloakId);
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnEmptyList_whenUserHasNoGames() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        given(userRepository.findOwnedGamesByKeycloakId(keycloakId)).willReturn(List.of());

        // Act
        List<GameId> result = userService.findOwnedGamesByKeycloakId(keycloakId);

        // Assert
        assertThat(result).isEmpty();
        then(userRepository).should().findOwnedGamesByKeycloakId(keycloakId);
    }
}
