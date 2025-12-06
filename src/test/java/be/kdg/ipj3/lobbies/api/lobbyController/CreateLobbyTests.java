package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.lobbies.api.dto.CreateLobbyDto;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class CreateLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ILobbyRepository lobbyRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
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

    private GameId createTestGame() {
        Game game = new Game(
                "Game",
                "Description",
                Money.of(new java.math.BigDecimal("19.99")),
                "https://test.com/service",
                "A rule",
                java.util.List.of(Category.NATIVE)
        );
        gameRepository.save(game);
        return game.getId();
    }

    @Test
    void createLobby_shouldReturnLobbyId_andPersistLobby_whenValidRequest() throws Exception {
        // Arrange
        createTestUser();
        GameId gameId = createTestGame();
        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        // Act & Assert
        String responseContent = mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Optional<Lobby> maybe = lobbyRepository.findAll()
                .stream()
                .filter(l -> l.getGame().equals(gameId))
                .findAny();

        Lobby created = maybe.orElseThrow(() ->
                new AssertionError("Lobby not found in DB after create, gameId=" + gameId)
        );

        assertThat(created.getMaxPlayers()).isEqualTo(2);
        assertThat(created.getGame()).isEqualTo(gameId);
        assertThat(created.getPassword()).isEqualTo("password");
        assertThat(created.getStatus()).isEqualTo(LobbyStatus.OPEN);
        assertThat(created.getPlayers()).isNotEmpty();
    }

    @Test
    void createLobby_shouldReturnNotFound_whenInvalidGameId() throws Exception {
        createTestUser();
        GameId gameId = GameId.create();  // not persisted
        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void createLobby_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }*/

    @Test
    void createLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        createTestUser();
        GameId gameId = createTestGame();
        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        mockMvc.perform(post("/api/lobbies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLobby_shouldReturnBadRequest_whenInvalidDto() throws Exception {
        createTestUser();
        GameId gameId = createTestGame();

        String invalidDto = """
            {
              "password": "password",
              "maxPlayers": 30,
              "gameId": "%s"
            }
            """.formatted(gameId.id());

        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDto))
                .andExpect(status().isBadRequest());
    }
}
