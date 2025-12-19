package be.kdg.ipj3.perks;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.domain.PerkType;
import be.kdg.ipj3.perks.shared.PerkId;
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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class PerkIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerkRepository perkRepository;

    @BeforeEach
    void setUp() {
        perkRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private void createTestPerks() {
        Perk perk1 = new Perk(
                new PerkId(UUID.randomUUID()),
                PerkType.BORDER,
                "Neon Border",
                "a nice black neo border to deckout your profilepage",
                100
        );

        Perk perk2 = new Perk(
                new PerkId(UUID.randomUUID()),
                PerkType.TITLE,
                "The Horse",
                "The horse title represents someone who loves the knight in chess",
                10
        );

        perkRepository.save(perk1);
        perkRepository.save(perk2);
    }

    @Test
    void getAllPerks_shouldReturnListOfPerks_whenPerksExist() throws Exception {
        // Arrange
        createTestPerks();

        // Act & Assert
        mockMvc.perform(get("/api/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Neon Border"))
                .andExpect(jsonPath("$[0].pointCost").value(100))
                .andExpect(jsonPath("$[1].name").value("The Horse"));
    }

    @Test
    void getAllPerks_shouldReturnEmptyList_whenNoPerksExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPerkById_shouldReturnPerk_whenPerkExists() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Perk perk = new Perk(
                new PerkId(id),
                PerkType.TITLE,
                "Invisible",
                "Cannot be seen by enemies",
                5
        );
        perkRepository.save(perk);

        // Act & Assert
        mockMvc.perform(get("/api/perks/{perkId}", id)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Invisibility"))
                .andExpect(jsonPath("$.pointCost").value(500));
    }

    @Test
    void getPerkById_shouldReturnNotFound_whenPerkDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/perks/{perkId}", UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }
}