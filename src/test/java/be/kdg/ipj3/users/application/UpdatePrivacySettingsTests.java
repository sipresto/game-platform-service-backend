package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Profile;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    void updatePrivacySettingsByKeycloakId_shouldUpdateVisibilityAndSave_whenValidUserProvided() {
        // Arrange
        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, mockUser);

        // Assert
        then(mockUser).should().updateVisibility(newVisibility);
        then(userRepository).should().save(mockUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldCallUpdateVisibilityExactlyOnce_whenInvoked() {
        // Arrange
        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, mockUser);

        // Assert
        then(mockUser).should().updateVisibility(newVisibility);
        then(mockUser).should(never()).updateProfile(any());
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldNotModifyOtherUserProperties() {
        // Arrange
        Visibility newVisibility = Visibility.PRIVATE;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, mockUser);

        // Assert
        then(mockUser).should().updateVisibility(newVisibility);
        then(mockUser).should(never()).addGame(any());
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldSaveUserWithUpdatedVisibility() {
        // Arrange
        Visibility newVisibility = Visibility.PUBLIC;
        User realUser = createTestUser();

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, realUser);

        // Assert
        then(userRepository).should().save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser).isSameAs(realUser);
        assertThat(savedUser.getVisibility()).isEqualTo(newVisibility);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldHandleMultipleVisibilityChanges() {
        // Arrange
        User realUser = createTestUser();

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PRIVATE, realUser);
        userService.updatePrivacySettingsByKeycloakId(Visibility.FRIENDS_ONLY, realUser);
        userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, realUser);

        // Assert
        assertThat(realUser.getVisibility()).isEqualTo(Visibility.PUBLIC);
        then(userRepository).should(times(3)).save(realUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldPreserveUserStateExceptVisibility() {
        // Arrange
        User realUser = createTestUser();
        String originalUsername = realUser.getUsername();
        Profile originalProfile = realUser.getProfile();
        List<?> originalGames = realUser.getOwnedGames();
        String originalKeycloakId = realUser.getKeycloakId();
        UserId originalId = realUser.getId();

        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, realUser);

        // Assert
        assertThat(realUser.getId()).isEqualTo(originalId);
        assertThat(realUser.getKeycloakId()).isEqualTo(originalKeycloakId);
        assertThat(realUser.getUsername()).isEqualTo(originalUsername);
        assertThat(realUser.getProfile()).isEqualTo(originalProfile);
        assertThat(realUser.getOwnedGames()).isEqualTo(originalGames);

        assertThat(realUser.getVisibility()).isEqualTo(newVisibility);
        then(userRepository).should().save(realUser);
    }

    @ParameterizedTest
    @EnumSource(Visibility.class)
    void updatePrivacySettingsByKeycloakId_shouldWorkWithAllVisibilityTypes(Visibility visibility) {
        // Arrange
        User realUser = createTestUser();

        // Act
        userService.updatePrivacySettingsByKeycloakId(visibility, realUser);

        // Assert
        assertThat(realUser.getVisibility()).isEqualTo(visibility);
        then(userRepository).should().save(realUser);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldWorkWithDifferentUserInstances() {
        // Arrange
        User user1 = createTestUserWithKeycloakId("user1");
        User user2 = createTestUserWithKeycloakId("user2");

        // Act
        userService.updatePrivacySettingsByKeycloakId(Visibility.PUBLIC, user1);
        userService.updatePrivacySettingsByKeycloakId(Visibility.PRIVATE, user2);

        // Assert
        assertThat(user1.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(user2.getVisibility()).isEqualTo(Visibility.PRIVATE);
        then(userRepository).should().save(user1);
        then(userRepository).should().save(user2);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldHandleUserCreatedFromKeycloak() {
        // Arrange
        User userFromKeycloak = User.createFromKeycloak("kc-id-123", "kc-username");
        Visibility newVisibility = Visibility.FRIENDS_ONLY;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, userFromKeycloak);

        // Assert
        assertThat(userFromKeycloak.getVisibility()).isEqualTo(newVisibility);
        then(userRepository).should().save(userFromKeycloak);
    }

    @Test
    void updatePrivacySettingsByKeycloakId_shouldWorkWithUserHavingEmptyGameList() {
        // Arrange
        User userWithNoGames = new User(
                UserId.create(),
                "test-id",
                "testuser",
                new Profile("Test User", "Test bio")
        );
        Visibility newVisibility = Visibility.PRIVATE;

        // Act
        userService.updatePrivacySettingsByKeycloakId(newVisibility, userWithNoGames);

        // Assert
        assertThat(userWithNoGames.getOwnedGames()).isEmpty();
        assertThat(userWithNoGames.getVisibility()).isEqualTo(newVisibility);
        then(userRepository).should().save(userWithNoGames);
    }

    private User createTestUser() {
        return createTestUserWithKeycloakId("test-keycloak-id");
    }

    private User createTestUserWithKeycloakId(String keycloakId) {
        return new User(
                UserId.create(),
                keycloakId,
                "testuser",
                new Profile("Test User", "Test bio")
        );
    }
}