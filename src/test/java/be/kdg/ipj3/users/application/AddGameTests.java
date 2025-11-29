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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class AddGameTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addGame_shouldAddGameToUser_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserId userDomainId = new UserId(userId);
        UUID gameId = UUID.randomUUID();
        GameId gameDomainId = new GameId(gameId);
        User user = new User(userDomainId, "keycloak-id", "testuser", new Profile("Test User", "Bio"));

        given(userRepository.findById(userDomainId)).willReturn(Optional.of(user));

        // Act
        userService.addGame(userDomainId, gameDomainId);

        // Assert
        then(userRepository).should().findById(userDomainId);
        then(userRepository).should().save(user);
        assertThat(user.getOwnedGames()).contains(gameDomainId); // This should now work
    }

    @Test
    void addGame_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserId userDomainId = new UserId(userId);
        GameId gameDomainId = new GameId(UUID.randomUUID());

        given(userRepository.findById(userDomainId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.addGame(userDomainId, gameDomainId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(userId.toString());

        then(userRepository).should().findById(userDomainId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }
}