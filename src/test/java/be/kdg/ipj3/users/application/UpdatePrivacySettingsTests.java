package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UpdatePrivacySettingsTests {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private User mockUser;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @Test
    void updatePrivacySettingsByKeycloakId_shouldUpdateVisibilityAndSave_whenUserExists() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, keycloakId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.FRIENDS_ONLY);
        then(userRepository).should().save(mockUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        String keycloakId = "non-existent-keycloak-id";
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, keycloakId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with keycloak id [" + keycloakId + "] not found");

        then(userRepository).should(never()).save(mockUser);
        then(mockUser).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldCallUpdateVisibilityExactlyOnce_whenInvoked() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, keycloakId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.FRIENDS_ONLY);
        then(mockUser).should(never()).updateProfile(any());
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldNotModifyOtherUserProperties() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PRIVATE, keycloakId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.PRIVATE);
        then(mockUser).should(never()).addGame(any());
        then(mockUser).should(never()).updateProfile(any());
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldPropagateKeycloakIdCorrectly() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        User realUser = createTestUserWithKeycloakId(keycloakId);
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(realUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, keycloakId);

        // Assert
        then(userRepository).should().save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(savedUser.getProfile_visibility()).isEqualTo(Visibility.PUBLIC);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldHandleMultipleVisibilityChanges() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PRIVATE, keycloakId);
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, keycloakId);
        userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, keycloakId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.PRIVATE);
        then(mockUser).should().updateVisibility(Visibility.FRIENDS_ONLY);
        then(mockUser).should().updateVisibility(Visibility.PUBLIC);
        then(userRepository).should(times(3)).save(mockUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldPreserveUserStateExceptVisibility() {
        // Arrange
        String keycloakId = "test-keycloak-id";
        User realUser = createTestUserWithKeycloakId(keycloakId);
        String originalUsername = realUser.getUsername();
        var originalProfile = realUser.getProfile();

        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(realUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, keycloakId);

        // Assert
        assertThat(realUser.getUsername()).isEqualTo(originalUsername);
        assertThat(realUser.getProfile()).isEqualTo(originalProfile);
        assertThat(realUser.getProfile_visibility()).isEqualTo(Visibility.FRIENDS_ONLY);
        then(userRepository).should().save(realUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldThrowIllegalArgumentException_whenKeycloakIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Keycloak id cannot be null or blank");
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldThrowIllegalArgumentException_whenKeycloakIdIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Keycloak id cannot be null or blank");
    }

    @ParameterizedTest
    @EnumSource(Visibility.class)
    void updatePrivacySettingsByKeycloakId_shouldWorkWithAllVisibilityTypes(Visibility visibility) {
        // Arrange
        String keycloakId = "test-keycloak-id";
        User realUser = createTestUserWithKeycloakId(keycloakId);
        given(userRepository.findByKeycloakId(keycloakId))
                .willReturn(Optional.of(realUser));

        // Act
        userService.updatePrivacySettingsByKeycloakId(visibility, keycloakId);

        // Assert
        assertThat(realUser.getProfile_visibility()).isEqualTo(visibility);
        then(userRepository).should().save(realUser);
    }

    private User createTestUserWithKeycloakId(String keycloakId) {
        return new User(
                UserId.create(),
                keycloakId,
                "testuser",
                java.util.List.of(),
                new be.kdg.ipj3.users.domain.Profile("Test User", "Test bio")
        );
    }
}