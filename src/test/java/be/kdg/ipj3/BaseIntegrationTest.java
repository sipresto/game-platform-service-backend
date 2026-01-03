package be.kdg.ipj3;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected UserRepository userRepository;
    @Autowired protected PerkRepository perkRepository;
    @Autowired protected ChatRepository chatRepository;
    @Autowired protected GameRepository gameRepository;
    @Autowired protected LobbyRepository lobbyRepository;
    @Autowired protected SessionRepository sessionRepository;
    @Autowired protected IPurchaseRepository purchaseRepository;

    @MockitoBean
    protected StripeService stripeService;

    @BeforeEach
    void baseSetUp() {
        sessionRepository.deleteAll();
        lobbyRepository.deleteAll();
        purchaseRepository.deleteAll();
        chatRepository.deleteAll();
        perkRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    // -- Auth Helper --
    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return validJwt("keycloak-id", "test-user");
    }

    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt(String subject, String username) {
        return jwt().jwt(b -> b.subject(subject).claim("preferred_username", username))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    // -- User Helper --
    protected User createTestUser() {
        return createTestUser("kc-" + UUID.randomUUID(), "test-user", "test@gmail.com");
    }

    protected User createTestUser(String keycloakId, String username, String email) {
        User user = new User(
                new UserId(UUID.randomUUID()), keycloakId, username, List.of(), List.of(), new UserCosmetics(),
                100, new Profile(username, "bio", null, null),
                VisibilitySettings.defaultPublic(), email
        );
        userRepository.save(user);
        return user;
    }

    // -- Game Helper --
    protected Game createTestGame() {
        return createTestGame("Standard Game", BigDecimal.valueOf(19.99));
    }

    protected Game createTestGame(String name, BigDecimal price) {
        Game game = new Game(
                GameId.create(), name, "Desc", Money.of(price),
                "/api", "/game", "Rules", List.of(), List.of(Category.NATIVE),
                "", Instant.now()
        );
        gameRepository.save(game);
        return game;
    }

    // -- Lobby Helper --
    protected Lobby createTestLobby(GameId gameId, UserId hostId) {
        Lobby lobby = new Lobby("password", 4, gameId, hostId);
        lobbyRepository.save(lobby);
        return lobby;
    }

    // -- Session Helper --
    protected Session createTestSession(UserId userId, Difficulty difficulty) {
        Session session = new Session(SessionId.create(), GameId.create(), List.of(userId), difficulty, SessionStatus.PLAYER_WIN, userId, Instant.now());
        sessionRepository.save(session);
        return session;
    }
}