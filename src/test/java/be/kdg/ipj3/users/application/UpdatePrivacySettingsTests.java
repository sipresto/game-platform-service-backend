package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

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

    private UserId testUserId;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testUserId = new UserId(testUuid);
    }

    @ParameterizedTest
    @EnumSource(Visibility.class)
    void updatePrivacySettings_shouldUpdateUserVisibilityAndSave_whenUserExists(Visibility visibility) {
        // Arrange
        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettings(visibility, testUserId);

        // Assert
        then(mockUser).should().updateVisibility(visibility);
        then(userRepository).should().save(mockUser);
    }

    @Test
    void updatePrivacySettings_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        given(userRepository.findById(testUserId))
                .willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updatePrivacySettings(Visibility.PUBLIC, testUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with userId")
                .hasMessageContaining(testUuid.toString());

        then(userRepository).should(never()).save(any());
        then(mockUser).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldCallUpdateVisibilityExactlyOnce_whenInvoked() {
        // Arrange
        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettings(Visibility.FRIENDS_ONLY, testUserId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.FRIENDS_ONLY);
        then(mockUser).should(never()).updateProfile(any());
    }

    @Test
    void updatePrivacySettings_shouldNotModifyOtherUserProperties() {
        // Arrange
        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettings(Visibility.PRIVATE, testUserId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.PRIVATE);
        then(mockUser).should(never()).addGame(any());
        then(mockUser).should(never()).updateProfile(any());
    }

    @Test
    void updatePrivacySettings_shouldPropagateUserIdCorrectly() {
        // Arrange
        User realUser = createTestUserWithId(testUserId);
        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(realUser));

        // Act
        userService.updatePrivacySettings(Visibility.PUBLIC, testUserId);

        // Assert
        then(userRepository).should().save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getId()).isEqualTo(testUserId);
        assertThat(savedUser.getProfile_visibility()).isEqualTo(Visibility.PUBLIC);
    }

    @Test
    void updatePrivacySettings_shouldHandleMultipleVisibilityChanges() {
        // Arrange
        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(mockUser));

        // Act
        userService.updatePrivacySettings(Visibility.PRIVATE, testUserId);
        userService.updatePrivacySettings(Visibility.FRIENDS_ONLY, testUserId);
        userService.updatePrivacySettings(Visibility.PUBLIC, testUserId);

        // Assert
        then(mockUser).should().updateVisibility(Visibility.PRIVATE);
        then(mockUser).should().updateVisibility(Visibility.FRIENDS_ONLY);
        then(mockUser).should().updateVisibility(Visibility.PUBLIC);
        then(userRepository).should(times(3)).save(mockUser);
    }

    @Test
    void updatePrivacySettings_shouldPreserveUserStateExceptVisibility() {
        // Arrange
        User realUser = createTestUserWithId(testUserId);
        String originalUsername = realUser.getUsername();
        var originalProfile = realUser.getProfile();

        given(userRepository.findById(testUserId))
                .willReturn(Optional.of(realUser));

        // Act
        userService.updatePrivacySettings(Visibility.FRIENDS_ONLY, testUserId);

        // Assert
        assertThat(realUser.getUsername()).isEqualTo(originalUsername);
        assertThat(realUser.getProfile()).isEqualTo(originalProfile);
        assertThat(realUser.getProfile_visibility()).isEqualTo(Visibility.FRIENDS_ONLY);
        then(userRepository).should().save(realUser);
    }

    @Test
    void updatePrivacySettings_shouldThrowExceptionWhenUserIdIsNull() {
        // Arrange
        UserId nullUserId = null;

        // Act & Assert
        assertThatThrownBy(() -> userService.updatePrivacySettings(Visibility.PUBLIC, nullUserId))
                .isInstanceOf(NotFoundException.class);
    }

    private User createTestUserWithId(UserId userId) {
        return new User(
                userId,
                "test-keycloak-id",
                "testuser",
                java.util.List.of(),
                new be.kdg.ipj3.users.domain.Profile("Test User", "Test bio")
        );
    }
}