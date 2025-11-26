/*
package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private IUserRepository userRepository;

    private UserService userService;

    private final String KEYCLOAK_ID = UUID.randomUUID().toString();
    private final String KEYCLOAK_USERNAME = "keycloakUser";

    private UserId userId;
    private User user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);

        userId = UserId.fromKeycloak(KEYCLOAK_ID);
        user = new User(userId, "testuser", Collections.emptyList());
        profile = new Profile(userId, "testuser", "Test User");
    }


    @Test
    void testGetUserWithGames_Found() {
        when(userRepository.getUserByIdWithGames(userId))
                .thenReturn(Optional.of(user));

        User result = userService.getUserWithGames(userId);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).getUserByIdWithGames(userId);
    }

    @Test
    void testGetUserWithGames_NotFound() {
        when(userRepository.getUserByIdWithGames(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserWithGames(userId)
        );
        verify(userRepository).getUserByIdWithGames(userId);
    }

    @Test
    void testRegisterOrGetUser_ExistingUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.registerOrGetUser(KEYCLOAK_ID, KEYCLOAK_USERNAME);

        assertEquals(user, result);
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).save(any(Profile.class));
    }

    @Test
    void testRegisterOrGetUser_NewUserRegistration() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);

        when(userRepository.save(userCaptor.capture())).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.save(profileCaptor.capture())).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.registerOrGetUser(KEYCLOAK_ID, KEYCLOAK_USERNAME);

        // Assert
        assertNotNull(result);
        assertEquals(KEYCLOAK_ID, result.getId().value());
        assertEquals(KEYCLOAK_USERNAME, result.getUsername());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).save(any(Profile.class));

        User savedUser = userCaptor.getValue();
        Profile savedProfile = profileCaptor.getValue();

        assertEquals(KEYCLOAK_ID, savedUser.getId().value());
        assertEquals(KEYCLOAK_USERNAME, savedProfile.getUsername());
    }

    @Test
    void testGetProfile_Found() {
        when(userRepository.findProfileByUserId(userId)).thenReturn(Optional.of(profile));

        Profile result = userService.getProfile(userId);

        assertEquals(profile, result);
        verify(userRepository).findProfileByUserId(userId);
    }

    @Test
    void testGetProfile_NotFound() {
        when(userRepository.findProfileByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.getProfile(userId)
        );
    }

    @Test
    void testGetUser_Found() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User result = userService.getUser(userId);
        assertEquals(user, result);
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUser(userId));
    }

    @Test
    void testUpdateUsername_Success() {
        String newUsername = "newUserName";

        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findProfileByUserId(userId)).thenReturn(Optional.of(profile));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        userService.updateUsername(userId, newUsername);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findProfileByUserId(userId);

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, times(1)).save(profileCaptor.capture());

        assertEquals(newUsername, userCaptor.getValue().getUsername());
        assertEquals(newUsername, profileCaptor.getValue().getUsername());
    }

    @Test
    void testUpdateUsername_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUsername(userId, "fail"));

        verify(userRepository, never()).save((User) any());
    }
}*/
