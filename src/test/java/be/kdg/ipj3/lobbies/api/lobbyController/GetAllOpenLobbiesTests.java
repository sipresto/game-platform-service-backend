package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetAllOpenLobbiesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LobbyRepository lobbyRepository;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private void createTestLobbies() {
        Lobby lobby1 = new Lobby(
                "password",
                4,
                GameId.create(),
                UserId.create()
        );

        Lobby lobby2 = new Lobby(
                "",
                2,
                GameId.create(),
                UserId.create()
        );

        lobbyRepository.save(lobby1);
        lobbyRepository.save(lobby2);
    }

    @Test
    void getAllOpenLobbies_shouldReturnListOfLobbies_whenLobbiesExist() throws Exception {
        // Arrange
        createTestLobbies();

        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1))
                .andExpect(jsonPath("$[0].isPrivate").value(true))
                .andExpect(jsonPath("$[1].isPrivate").value(false));
    }

    @Test
    void getAllOpenLobbies_shouldReturnEmptyList_whenNoLobbiesExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void getAllOpenLobbies_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        // Arrange
        createTestLobbies();

        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }*/

    @Test
    void getAllOpenLobbies_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        createTestLobbies();

        // Act & Assert
        mockMvc.perform(get("/api/lobbies"))
                .andExpect(status().isUnauthorized());
    }
}
