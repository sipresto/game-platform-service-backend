package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Mock
    private IUserRepository userRepository;

    private UserService userService;

    private UserId userId;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        userId = new UserId(UUID.randomUUID().toString());
        user = new User(userId, "testuser", List.of());
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

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserWithGames(userId)
        );

        assertTrue(ex.getMessage().contains(userId.toString()));
        verify(userRepository).getUserByIdWithGames(userId);
    }
}
