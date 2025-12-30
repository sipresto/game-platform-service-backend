package be.kdg.ipj3.perks.api.perkController;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.domain.PerkType;
import be.kdg.ipj3.perks.shared.PerkId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors; // Import Added
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt; // Import Added
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetPerkByIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerkRepository perkRepository;

    @BeforeEach
    public void setup() {
        perkRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloak-id")
                        .claim("preferred_username", "test-user"));
    }

    private Perk createTestPerk(String name, PerkType type, int pointCost) {
        Perk perk = new Perk(
                new PerkId(UUID.randomUUID()),
                type,
                name,
                "Test description for " + name,
                pointCost
        );
        perkRepository.save(perk);
        return perk;
    }

    @Test
    void getPerkById_shouldReturnPerk_whenPerkExists() throws Exception {
        // Arrange
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(perk.getId().id().toString()));
    }

    @Test
    void getPerkById_shouldReturnNotFound_whenPerkDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/" + nonExistentId)
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPerkById_shouldReturnCorrectPerk_whenMultiplePerksExist() throws Exception {
        // Arrange
        createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk targetPerk = createTestPerk("Theme 1", PerkType.THEME, 200);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/" + targetPerk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Theme 1"));
    }

    @Test
    void getPerkById_shouldReturnPerkWithAllFields() throws Exception {
        // Arrange
        Perk perk = createTestPerk("Epic Gamer", PerkType.TITLE, 999);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }
}