package be.kdg.ipj3.users.application;

import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UpdatePrivacySettingsTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void updatePrivacySettings_shouldUpdateVisibilityAndSave_whenValidUserProvided() {
        // Arrange
        User user = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio"),
                0
        );
        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, user);

        // Assert
        assertThat(user.getVisibility()).isEqualTo(newVisibility);
        then(userRepository).should().save(user);
    }

    @Test
    void updatePrivacySettings_shouldSaveUserWithUpdatedVisibility_whenRealUser() {
        // Arrange
        User user = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio"),
                0
        );
        Visibility newVisibility = Visibility.PUBLIC;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, user);

        // Assert
        then(userRepository).should().save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved).isSameAs(user);
        assertThat(saved.getVisibility()).isEqualTo(newVisibility);
    }

    @Test
    void updatePrivacySettings_shouldAllowMultipleVisibilityChanges() {
        // Arrange
        User user = new User(
                new UserId(UUID.randomUUID()),
                "kc-id",
                "username",
                new Profile("Display", "Bio"),
                0
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
                new Profile("Display", "Bio"),
                0
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
