package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePrivacySettingsTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void updatePrivacySettings_shouldUpdateVisibilityAndSave_whenValidUserProvided() {
        // Arrange
        User user = mock(User.class);
        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, user);

        // Assert
        then(user).should().updateVisibility(newVisibility);
        then(userRepository).should().save(user);
    }

    @Test
    void updatePrivacySettings_shouldSaveUserWithUpdatedVisibility_whenRealUser() {
        // Arrange
        User realUser = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio")
        );
        Visibility newVisibility = Visibility.PUBLIC;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, realUser);

        // Assert
        then(userRepository).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved).isSameAs(realUser);
        assertThat(saved.getVisibility()).isEqualTo(newVisibility);
    }

    @Test
    void updatePrivacySettings_shouldAllowMultipleVisibilityChanges() {
        // Arrange
        User user = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio")
        );

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PRIVATE, user);
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, user);
        userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, user);

        // Assert
        assertThat(user.getVisibility()).isEqualTo(Visibility.PUBLIC);
        then(userRepository).should(times(3)).save(user);
    }

    @Test
    void updatePrivacySettings_shouldPreserveOtherUserFields_exceptVisibility() {
        // Arrange
        User user = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio")
        );
        UserId originalId = user.getId();
        String originalKeycloakId = user.getKeycloakId();
        String originalUsername = user.getUsername();
        Profile originalProfile = user.getProfile();
        var originalGames = user.getOwnedGames();

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, user);

        // Assert
        assertThat(user.getId()).isEqualTo(originalId);
        assertThat(user.getKeycloakId()).isEqualTo(originalKeycloakId);
        assertThat(user.getUsername()).isEqualTo(originalUsername);
        assertThat(user.getProfile()).isEqualTo(originalProfile);
        assertThat(user.getOwnedGames()).isEqualTo(originalGames);

        assertThat(user.getVisibility()).isEqualTo(Visibility.FRIENDS_ONLY);
        then(userRepository).should().save(user);
    }
}
