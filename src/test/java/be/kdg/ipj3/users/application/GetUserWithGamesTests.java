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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class GetUserWithGamesTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserWithGames_shouldReturnUser_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserId userDomainId = new UserId(userId);
        User user = new User(userDomainId, "keycloak-id", "testuser",
                List.of(new GameId(UUID.randomUUID())), new Profile("Test User", "Bio"));

        given(userRepository.findByIdWithGames(userDomainId)).willReturn(Optional.of(user));

        // Act
        User result = userService.getUserWithGames(userDomainId);

        // Assert
        assertThat(result).isEqualTo(user);
        then(userRepository).should().findByIdWithGames(userDomainId);
    }

    @Test
    void getUserWithGames_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserId userDomainId = new UserId(userId);

        given(userRepository.findByIdWithGames(userDomainId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserWithGames(userDomainId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(userId.toString());

        then(userRepository).should().findByIdWithGames(userDomainId);
    }
}