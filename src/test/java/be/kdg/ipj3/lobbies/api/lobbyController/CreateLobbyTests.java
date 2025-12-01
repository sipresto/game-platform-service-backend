package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.lobbies.api.dto.CreateLobbyDto;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                Money.of(BigDecimal.valueOf(19.99)),
                "https://test.com/service",
                List.of(Category.NATIVE)
        );
        gameRepository.save(game);

        return game.getId();
    }

    @Test
    void createLobby_shouldReturnLobbyId_whenValidRequest() throws Exception {
        // Arrange
        createTestUser();

        GameId gameId = createTestGame();

        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createLobby_shouldReturnNotFound_whenInvalidGameId() throws Exception {
        // Arrange
        createTestUser();

        GameId gameId = GameId.create();

        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
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
        // Arrange
        createTestUser();

        GameId gameId = createTestGame();

        CreateLobbyDto dto = new CreateLobbyDto("password", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLobby_shouldReturnBadRequest_whenInvalidDto() throws Exception {
        // Arrange
        createTestUser();

        GameId gameId = createTestGame();

        String dto = """
                {
                  "password": "password",
                  "maxPlayers": 30,
                  "gameId": "%s"
                }
                """.formatted(gameId.id());

        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(dto))
                .andExpect(status().isBadRequest());
    }
}
