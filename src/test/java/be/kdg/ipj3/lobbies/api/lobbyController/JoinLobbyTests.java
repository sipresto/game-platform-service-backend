package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.api.dto.JoinLobbyDto;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class JoinLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LobbyService lobbyService;

    @MockitoBean
    private UserService userService;

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void joinLobby_shouldReturnOk_whenStateIsValid() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();
        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        then(lobbyService).should().joinLobby(lobbyId, userId, "password");
        then(userService).should().registerOrGetUser("keycloak-id", "test-user");
    }

    @Test
    void joinLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        then(userService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
    }

    @Test
    void joinLobby_shouldReturnConflict_whenLobbyFull() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();
        JoinLobbyDto dto = new JoinLobbyDto("password");

        willThrow(new IllegalStateException("Lobby is full."))
                .given(lobbyService)
                .joinLobby(any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        then(lobbyService).should().joinLobby(lobbyId, userId, "password");
    }

    @Test
    void joinLobby_shouldReturnBadRequest_whenWrongPassword() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();
        JoinLobbyDto dto = new JoinLobbyDto("wrong");

        willThrow(new IllegalArgumentException("Invalid lobby password."))
                .given(lobbyService)
                .joinLobby(any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        then(lobbyService).should().joinLobby(lobbyId, userId, "wrong");
    }

    @Test
    void joinLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();
        JoinLobbyDto dto = new JoinLobbyDto("password");

        willThrow(new NotFoundException("Lobby not found"))
                .given(lobbyService)
                .joinLobby(any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        then(lobbyService).should().joinLobby(lobbyId, userId, "password");
    }
}
