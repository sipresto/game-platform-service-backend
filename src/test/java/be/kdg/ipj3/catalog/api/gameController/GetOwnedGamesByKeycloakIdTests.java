package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.domain.GameRepository;
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
public class GetOwnedGamesByKeycloakIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    // TODO: Test when games owned

    // TODO: Test when no games owned

    // TODO: Test when user not exist
}
