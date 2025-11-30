package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
public class LeaveLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LobbyService lobbyService;

    @MockitoBean
    private UserService userService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void leaveLobby_shouldReturnOk_whenStateIsValid() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/leave", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(userService).should().registerOrGetUser("keycloak-id", "test-user");
        then(lobbyService).should().leaveLobby(lobbyId, userId);
    }

    @Test
    void leaveLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/leave", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        then(userService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
    }

    @Test
    void leaveLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        LobbyId lobbyId = LobbyId.create();

        willThrow(new NotFoundException("Lobby not found"))
                .given(lobbyService)
                .leaveLobby(any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/leave", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        then(lobbyService).should().leaveLobby(lobbyId, userId);
    }
}
