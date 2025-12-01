package be.kdg.ipj3.users.application;

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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class UpdateProfileTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateProfile_shouldUpdateProfile_whenUserExists() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String displayName = "Updated Display Name";
        String bio = "Updated bio";
        User user = new User(new UserId(UUID.randomUUID()), keycloakId, "oldusername", new Profile("Old Display", "Old bio"));

        given(userRepository.findByKeycloakId(keycloakId)).willReturn(Optional.of(user));

        // Act
        userService.updateProfile(keycloakId, displayName, bio);

        // Assert
        then(userRepository).should().findByKeycloakId(keycloakId);
        then(userRepository).should().save(user);

        assertThat(user.getProfile().displayName()).isEqualTo(displayName);
        assertThat(user.getProfile().bio()).isEqualTo(bio);
        assertThat(user.getUsername()).isEqualTo(displayName);
    }

    @Test
    void updateProfile_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        String keycloakId = "non-existent-keycloak-id";

        given(userRepository.findByKeycloakId(keycloakId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(keycloakId, "Display Name", "Bio"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(keycloakId);

        then(userRepository).should().findByKeycloakId(keycloakId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void updateProfile_shouldThrowIllegalArgumentException_whenKeycloakIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(null, "Display Name", "Bio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keycloak id cannot be null or blank");
    }

    @Test
    void updateProfile_shouldThrowIllegalArgumentException_whenKeycloakIdIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile("", "Display Name", "Bio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keycloak id cannot be null or blank");
    }
}