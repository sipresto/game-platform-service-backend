package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AddGameTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addGame_shouldAddGameToUser_whenUserExists() {
        // Arrange
        UUID userUuid = UUID.randomUUID();
        UserId userId = new UserId(userUuid);
        GameId gameId = new GameId(UUID.randomUUID());
        User user = new User(userId, "kc-id", "testuser", new Profile("Test", "Bio"));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // Act
        userService.addGame(userId, gameId);

        // Assert
        then(userRepository).should().findById(userId);
        then(userRepository).should().save(user);
        assertThat(user.getOwnedGames()).contains(gameId);
    }

    @Test
    void addGame_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        UUID userUuid = UUID.randomUUID();
        UserId userId = new UserId(userUuid);
        GameId gameId = new GameId(UUID.randomUUID());

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.addGame(userId, gameId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(userUuid.toString());

        then(userRepository).should().findById(userId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }
}
