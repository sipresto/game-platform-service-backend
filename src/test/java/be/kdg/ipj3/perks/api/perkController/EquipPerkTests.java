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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class EquipPerkTests {

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

    @Test
    void equipPerk_shouldEquipBorder_whenUserOwnsPerk() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);
        purchasePerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(perk.getId());
    }

    @Test
    void equipPerk_shouldEquipTheme_whenUserOwnsPerk() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Dark Theme", PerkType.THEME, 200);
        purchasePerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isEqualTo(perk.getId());
    }

    @Test
    void equipPerk_shouldEquipTitle_whenUserOwnsPerk() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Epic Gamer", PerkType.TITLE, 150);
        purchasePerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedTitleId()).isEqualTo(perk.getId());
    }

    @Test
    void equipPerk_shouldReturnBadRequest_whenUserDoesNotOwnPerk() throws Exception {
        // Arrange
        createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);

        // Act & Assert
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void equipPerk_shouldReplaceEquippedPerk_whenEquippingSameType() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk perk2 = createTestPerk("Border 2", PerkType.BORDER, 150);
        purchasePerk(user, perk1);
        purchasePerk(user, perk2);

        mockMvc.perform(post("/api/platform/perks/" + perk1.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk2.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(perk2.getId());
    }

    @Test
    void equipPerk_shouldAllowEquipping_differentPerkTypes() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border = createTestPerk("Cool Border", PerkType.BORDER, 100);
        Perk theme = createTestPerk("Dark Theme", PerkType.THEME, 200);
        Perk title = createTestPerk("Epic Gamer", PerkType.TITLE, 150);
        purchasePerk(user, border);
        purchasePerk(user, theme);
        purchasePerk(user, title);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + border.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/" + theme.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/" + title.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(border.getId());
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isEqualTo(theme.getId());
        assertThat(updatedUser.getCosmetics().getEquippedTitleId()).isEqualTo(title.getId());
    }

    @Test
    void equipPerk_shouldNotAffectOtherPerkTypes_whenEquipping() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border = createTestPerk("Cool Border", PerkType.BORDER, 100);
        Perk theme1 = createTestPerk("Theme 1", PerkType.THEME, 200);
        Perk theme2 = createTestPerk("Theme 2", PerkType.THEME, 250);
        purchasePerk(user, border);
        purchasePerk(user, theme1);
        purchasePerk(user, theme2);

        mockMvc.perform(post("/api/platform/perks/" + border.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/" + theme1.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Act
        mockMvc.perform(post("/api/platform/perks/" + theme2.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(border.getId());
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isEqualTo(theme2.getId());
    }
}