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
public class GetAllPerksTests {

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
    void getAllPerks_shouldReturnEmptyList_whenNoPerksExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllPerks_shouldReturnAllPerks_whenPerksExist() throws Exception {
        // Arrange
        createTestPerk("Cool Border", PerkType.BORDER, 100);
        createTestPerk("Dark Theme", PerkType.THEME, 200);
        createTestPerk("Epic Title", PerkType.TITLE, 150);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getAllPerks_shouldReturnPerksWithCorrectData() throws Exception {
        // Arrange
        Perk perk = createTestPerk("Golden Border", PerkType.BORDER, 500);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(perk.getId().id().toString()))
                .andExpect(jsonPath("$[0].name").value("Golden Border"));
    }

    @Test
    void getAllPerks_shouldReturnAllPerkTypes() throws Exception {
        // Arrange
        createTestPerk("Border Perk", PerkType.BORDER, 100);
        createTestPerk("Theme Perk", PerkType.THEME, 200);
        createTestPerk("Title Perk", PerkType.TITLE, 150);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.type == 'BORDER')]").exists())
                .andExpect(jsonPath("$[?(@.type == 'THEME')]").exists())
                .andExpect(jsonPath("$[?(@.type == 'TITLE')]").exists());
    }
}