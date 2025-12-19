package be.kdg.ipj3.users.application;

import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class RegisterOrGetUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerOrGetUser_shouldReturnExistingUser_whenUserAlreadyExists() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String testGmail = "Test@gmail.com";
        User existingUser = new User(
                new UserId(UUID.randomUUID()),
                keycloakId,
                keycloakUsername,
                new Profile("Existing User", "Bio"),
                0,
                testGmail
        );

        given(userRepository.findByKeycloakId(keycloakId)).willReturn(Optional.of(existingUser));

        // Act
        User result = userService.registerOrGetUser(keycloakId, keycloakUsername, testGmail);

        // Assert
        assertThat(result).isSameAs(existingUser);
        then(userRepository).should().findByKeycloakId(keycloakId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void registerOrGetUser_shouldCreateNewUser_whenUserDoesNotExist() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String testGmail = "Test@gmail.com";

        given(userRepository.findByKeycloakId(keycloakId)).willReturn(Optional.empty());

        // Act
        User result = userService.registerOrGetUser(keycloakId, keycloakUsername, testGmail);

        // Assert
        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getUsername()).isEqualTo(keycloakUsername);
        assertThat(result.getProfile().displayName()).isEqualTo(keycloakUsername);
        assertThat(result.getProfile().bio()).isEmpty();

        then(userRepository).should().findByKeycloakId(keycloakId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(savedUser.getUsername()).isEqualTo(keycloakUsername);
    }

    @Test
    void registerOrGetUser_shouldThrowIllegalArgumentException_whenKeycloakIdIsNull() {
        assertThatThrownBy(() -> userService.registerOrGetUser(null, "testuser", "Test@gmail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keycloak id cannot be null or blank");
    }

    @Test
    void registerOrGetUser_shouldThrowIllegalArgumentException_whenKeycloakIdIsBlank() {
        assertThatThrownBy(() -> userService.registerOrGetUser("", "testuser", "Test@gmail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keycloak id cannot be null or blank");
    }
}
