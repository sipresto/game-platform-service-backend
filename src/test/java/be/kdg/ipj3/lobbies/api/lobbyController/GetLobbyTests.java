package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    // TODO: Test when lobby exists

    // TODO: Test when lobby does not exist
}
