package be.kdg.ipj3.perks.api.perkController;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkRepository;
import be.kdg.ipj3.perks.domain.PerkType;
import be.kdg.ipj3.perks.shared.PerkId;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetUserPerksTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerkRepository perkRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        perkRepository.deleteAll();
        userRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloak-id")
                        .claim("preferred_username", "test-user"));
    }

    private User createTestUser(String keycloakId, String username, String email, int points) {
        User user = User.createFromKeycloak(keycloakId, username, email);
        user.addPoints(points);
        userRepository.save(user);
        return user;
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

    private void purchasePerk(User user, Perk perk) {
        user.addPerk(perk.getId());
        userRepository.save(user);
    }

    private void equipPerk(User user, Perk perk) {
        switch (perk.getType()) {
            case BORDER -> user.equipBorder(perk.getId());
            case THEME -> user.equipTheme(perk.getId());
            case TITLE -> user.equipTitle(perk.getId());
        }
        userRepository.save(user);
    }

    @Test
    void getUserPerks_shouldReturnEmptyList_whenNoPerksExist() throws Exception {
        // Arrange
        createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserPerks_shouldReturnAllPerks_withPurchasedFalse_whenUserOwnsNone() throws Exception {
        // Arrange
        createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk perk2 = createTestPerk("Theme 1", PerkType.THEME, 200);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].purchased").value(false))
                .andExpect(jsonPath("$[0].equipped").value(false));
    }

    @Test
    void getUserPerks_shouldShowPurchasedTrue_forOwnedPerks() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk perk2 = createTestPerk("Theme 1", PerkType.THEME, 200);
        purchasePerk(user, perk1);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.perkId == '" + perk1.getId().id() + "')].purchased").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + perk1.getId().id() + "')].equipped").value(false))
                .andExpect(jsonPath("$[?(@.perkId == '" + perk2.getId().id() + "')].purchased").value(false));
    }

    @Test
    void getUserPerks_shouldShowEquippedTrue_forEquippedPerks() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk perk2 = createTestPerk("Border 2", PerkType.BORDER, 150);
        purchasePerk(user, perk1);
        purchasePerk(user, perk2);
        equipPerk(user, perk1);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.perkId == '" + perk1.getId().id() + "')].purchased").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + perk1.getId().id() + "')].equipped").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + perk2.getId().id() + "')].equipped").value(false));
    }

    @Test
    void getUserPerks_shouldShowAllPerkTypes_withCorrectStatus() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk theme = createTestPerk("Theme 1", PerkType.THEME, 200);
        Perk title = createTestPerk("Title 1", PerkType.TITLE, 150);
        Perk unownedBorder = createTestPerk("Border 2", PerkType.BORDER, 300);

        purchasePerk(user, border);
        purchasePerk(user, theme);
        purchasePerk(user, title);
        equipPerk(user, border);
        equipPerk(user, theme);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[?(@.perkId == '" + border.getId().id() + "')].equipped").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + theme.getId().id() + "')].equipped").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + title.getId().id() + "')].equipped").value(false))
                .andExpect(jsonPath("$[?(@.perkId == '" + unownedBorder.getId().id() + "')].purchased").value(false));
    }

    @Test
    void getUserPerks_shouldIncludeAllPerkDetails() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);
        purchasePerk(user, perk);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].perkId").value(perk.getId().id().toString()))
                .andExpect(jsonPath("$[0].name").value("Cool Border"))
                .andExpect(jsonPath("$[0].type").value("BORDER"))
                .andExpect(jsonPath("$[0].pointCost").value(100))
                .andExpect(jsonPath("$[0].purchased").exists())
                .andExpect(jsonPath("$[0].equipped").exists());
    }

    @Test
    void getUserPerks_shouldOnlyShowOneEquipped_perPerkType() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk border2 = createTestPerk("Border 2", PerkType.BORDER, 150);
        Perk border3 = createTestPerk("Border 3", PerkType.BORDER, 200);

        purchasePerk(user, border1);
        purchasePerk(user, border2);
        purchasePerk(user, border3);
        equipPerk(user, border2);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.perkId == '" + border1.getId().id() + "')].equipped").value(false))
                .andExpect(jsonPath("$[?(@.perkId == '" + border2.getId().id() + "')].equipped").value(true))
                .andExpect(jsonPath("$[?(@.perkId == '" + border3.getId().id() + "')].equipped").value(false));
    }

    @Test
    void getUserPerks_shouldUpdateStatus_afterPurchaseAndEquip() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Border 1", PerkType.BORDER, 100);

        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].purchased").value(false))
                .andExpect(jsonPath("$[0].equipped").value(false));

        purchasePerk(user, perk);
        equipPerk(user, perk);

        // Act & Assert
        mockMvc.perform(get("/api/platform/perks/user")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].purchased").value(true))
                .andExpect(jsonPath("$[0].equipped").value(true));
    }
}