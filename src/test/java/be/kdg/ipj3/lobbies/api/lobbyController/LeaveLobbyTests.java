package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class LeaveLobbyTests {

    @Autowired
    private MockMvc mockMvc;

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

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private LobbyId createTestLobbyWithUser(UserId userId) {
        Lobby lobby = new Lobby(
                "password",
                2,
                GameId.create(),
                userId
        );
        lobbyRepository.save(lobby);
        return lobby.getId();
    }

    @Test
    void leaveLobby_shouldReturnOk_andLobbyShouldNotContainUser_whenStateIsValid() throws Exception {
        // Arrange
        UserId userId = createTestUser();
        LobbyId lobbyId = createTestLobbyWithUser(userId);

        // Act
        mockMvc.perform(post("/api/platform/lobbies/{id}/leave", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // --- extra: verify DB / repository state
        Optional<Lobby> opt = lobbyRepository.findById(lobbyId);
        Lobby updated = opt.orElseThrow(() ->
                new AssertionError("Lobby not found after leave, id=" + lobbyId)
        );

        assertThat(updated.getPlayers())
                .doesNotContain(userId);
    }

    @Test
    void leaveLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        UserId userId = createTestUser();

        mockMvc.perform(post("/api/platform/lobbies/{id}/leave", java.util.UUID.randomUUID())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void leaveLobby_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        LobbyId lobbyId = createTestLobbyWithUser(UserId.create());

        mockMvc.perform(post("/api/platform/lobbies/{id}/leave", lobbyId.id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }*/

    @Test
    void leaveLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        UserId someUserId = UserId.create();
        LobbyId lobbyId = createTestLobbyWithUser(someUserId);

        mockMvc.perform(post("/api/platform/lobbies/{id}/leave", lobbyId.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
