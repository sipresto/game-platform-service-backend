package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.api.dto.JoinLobbyDto;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

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

    @Autowired
    private ILobbyRepository lobbyRepository;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private void createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user");
        userRepository.save(user);
    }

    private LobbyId createTestLobby() {
        return createTestLobby(2, true);
    }

    private LobbyId createTestLobby(int maxPlayers, boolean markOpen) {
        Lobby lobby = new Lobby(
                "password",
                maxPlayers,
                GameId.create(),
                UserId.create()
        );

        if (!markOpen) {
            lobby.markAsInGame("gameUrl");
        }

        lobbyRepository.save(lobby);

        return lobby.getId();
    }

    @Test
    void joinLobby_shouldReturnOk_whenStateIsValid() throws Exception {
        // Arrange
        createTestUser();

        LobbyId lobbyId = createTestLobby();

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void joinLobby_shouldReturnConflict_whenLobbyNotOpen() throws Exception {
        // Arrange
        createTestUser();

        LobbyId lobbyId = createTestLobby(2, false);

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void joinLobby_shouldReturnConflict_whenLobbyFull() throws Exception {
        // Arrange
        createTestUser();

        LobbyId lobbyId = createTestLobby(1, true);

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void joinLobby_shouldReturnBadRequest_whenWrongPassword() throws Exception {
        // Arrange
        createTestUser();

        LobbyId lobbyId = createTestLobby();

        JoinLobbyDto dto = new JoinLobbyDto("pass");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void joinLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        createTestUser();

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", UUID.randomUUID())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void joinLobby_shouldReturnNotFound_whenUserNorRegistered() throws Exception {
        // Arrange
        LobbyId lobbyId = createTestLobby();

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }*/

    @Test
    void joinLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        LobbyId lobbyId = createTestLobby();

        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
