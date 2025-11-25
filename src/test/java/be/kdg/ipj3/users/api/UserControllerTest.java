/*
package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GameService gameService;

    @Mock
    private Jwt mockJwt;

    private UserController userController;

    private final String KEYCLOAK_ID = UUID.randomUUID().toString();
    private final String KEYCLOAK_USERNAME = "keycloakUser";
    private UserId userId;
    private User user;
    private Profile profile;
    private GameId game1Id;
    private Game game1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService, gameService);
        userId = new UserId(KEYCLOAK_ID);
        game1Id = GameId.create();

        user = new User(userId, "john", of(game1Id));
        profile = new Profile(userId, "john", "John Doe");

        game1 = new Game(
                game1Id,
                "Test Game",
                "Desc",
                Money.of(BigDecimal.valueOf(9.99)),
                of(),
                of(Category.BOARDGAME)
        );

        when(mockJwt.getSubject()).thenReturn(KEYCLOAK_ID);
        when(mockJwt.getClaim("preferred_username")).thenReturn(KEYCLOAK_USERNAME);
    }

    @Test
    void testGetUserProfile_Success() {
        when(userService.registerOrGetUser(KEYCLOAK_ID, KEYCLOAK_USERNAME)).thenReturn(user);
        when(userService.getProfile(userId)).thenReturn(profile);

        ResponseEntity<ProfileDto> response = userController.getUserProfile(mockJwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProfileDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(profile.getDisplayName(), dto.displayName());

        verify(userService, times(1)).registerOrGetUser(KEYCLOAK_ID, KEYCLOAK_USERNAME);
        verify(userService, times(1)).getProfile(userId);
    }

    @Test
    void testUpdateUsername_Success() {
        String newUsername = "new_alice";
        Map<String, String> requestBody = Map.of("username", newUsername);

        ArgumentCaptor<UserId> userIdCaptor = ArgumentCaptor.forClass(UserId.class);

        ResponseEntity<Map<String, String>> response =
                userController.updateUsername(mockJwt, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username updated successfully", response.getBody().get("message"));

        verify(userService, times(1)).updateUsername(userIdCaptor.capture(), eq(newUsername));
        assertEquals(KEYCLOAK_ID, userIdCaptor.getValue().value());
    }

    @Test
    void testUpdateUsername_BadRequest_EmptyUsername() {
        Map<String, String> requestBody = Map.of("username", "");

        ResponseEntity<Map<String, String>> response =
                userController.updateUsername(mockJwt, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username cannot be empty", response.getBody().get("error"));
        verify(userService, never()).updateUsername(any(), any());
    }

    @Test
    void testUpdateUsername_BadRequest_NullUsername() {
        Map<String, String> requestBody = Collections.emptyMap();

        ResponseEntity<Map<String, String>> response =
                userController.updateUsername(mockJwt, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username cannot be empty", response.getBody().get("error"));
        verify(userService, never()).updateUsername(any(), any());
    }

    @Test
    void testGetUserWithGames_WithOwnedGames() {
        when(userService.getUserWithGames(userId)).thenReturn(user);
        when(gameService.findGameById(game1Id)).thenReturn(game1);

        ResponseEntity<UserDto> response =
                userController.getUserWithGames(UUID.fromString(userId.value()));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("john", dto.username());
        assertEquals(1, dto.gameDto().size());

        verify(userService, times(1)).getUserWithGames(userId);
        verify(gameService, times(1)).findGameById(game1Id);
    }

    @Test
    void testGetUserWithGames_NoGames() {
        User userNoGames = new User(userId, "john", of());

        when(userService.getUserWithGames(userId)).thenReturn(userNoGames);

        ResponseEntity<UserDto> response =
                userController.getUserWithGames(UUID.fromString(userId.value()));

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().gameDto().size());
        verify(gameService, never()).findGameById(any());
    }

    @Test
    void testGetUserWithGames_UserNotFound() {
        when(userService.getUserWithGames(userId))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> userController.getUserWithGames(UUID.fromString(userId.value()))
        );
    }
}*/
