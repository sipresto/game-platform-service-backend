package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.lobbies.api.dto.JoinLobbyDto;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class JoinLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

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

    private UserId createTestUserAndReturnId() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private LobbyId createTestLobby(int maxPlayers, boolean markOpen) {
        Lobby lobby = new Lobby(
                "password",
                maxPlayers,
                GameId.create(),
                UserId.create()     // host
        );

        if (!markOpen) {
            lobby.markAsInGame("gameUrl");
        }

        lobbyRepository.save(lobby);
        return lobby.getId();
    }

    @Test
    void joinLobby_shouldReturnOk_andLobbyShouldContainUser_whenStateIsValid() throws Exception {
        // Arrange
        UserId userId = createTestUserAndReturnId();
        LobbyId lobbyId = createTestLobby(2, true);
        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Optional<Lobby> opt = lobbyRepository.findById(lobbyId);
        Lobby updatedLobby = opt.orElseThrow(
                () -> new AssertionError("Lobby not found after join, id=" + lobbyId)
        );

        assertThat(updatedLobby.getPlayers())
                .contains(userId);
    }

    @Test
    void joinLobby_shouldReturnConflict_whenLobbyNotOpen() throws Exception {
        createTestUserAndReturnId();
        LobbyId lobbyId = createTestLobby(2, false);
        JoinLobbyDto dto = new JoinLobbyDto("password");

        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void joinLobby_shouldReturnConflict_whenLobbyFull() throws Exception {
        createTestUserAndReturnId();
        LobbyId lobbyId = createTestLobby(1, true);
        JoinLobbyDto dto = new JoinLobbyDto("password");

        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void joinLobby_shouldReturnBadRequest_whenWrongPassword() throws Exception {
        createTestUserAndReturnId();
        LobbyId lobbyId = createTestLobby(2, true);
        JoinLobbyDto dto = new JoinLobbyDto("wrong-password");

        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void joinLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        createTestUserAndReturnId();
        JoinLobbyDto dto = new JoinLobbyDto("password");

        mockMvc.perform(post("/api/lobbies/{id}/join", UUID.randomUUID())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void joinLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        LobbyId lobbyId = createTestLobby(2, true);
        JoinLobbyDto dto = new JoinLobbyDto("password");

        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
