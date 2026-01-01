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
public class UnequipPerkTests {

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

    private void purchaseAndEquipPerk(User user, Perk perk) {
        user.addPerk(perk.getId());
        switch (perk.getType()) {
            case BORDER -> user.equipBorder(perk.getId());
            case THEME -> user.equipTheme(perk.getId());
            case TITLE -> user.equipTitle(perk.getId());
        }
        userRepository.save(user);
    }

    @Test
    void unequipPerk_shouldUnequipBorder_whenBorderEquipped() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);
        purchaseAndEquipPerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isNull();
        assertThat(updatedUser.hasPerk(perk.getId())).isTrue();
    }

    @Test
    void unequipPerk_shouldUnequipTheme_whenThemeEquipped() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Dark Theme", PerkType.THEME, 200);
        purchaseAndEquipPerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isNull();
        assertThat(updatedUser.hasPerk(perk.getId())).isTrue();
    }

    @Test
    void unequipPerk_shouldUnequipTitle_whenTitleEquipped() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Epic Gamer", PerkType.TITLE, 150);
        purchaseAndEquipPerk(user, perk);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedTitleId()).isNull();
        assertThat(updatedUser.hasPerk(perk.getId())).isTrue();
    }

    @Test
    void unequipPerk_shouldSucceed_whenPerkNotEquipped() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);
        user.addPerk(perk.getId());
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isNull();
    }

    @Test
    void unequipPerk_shouldNotAffectOtherPerkTypes() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border = createTestPerk("Cool Border", PerkType.BORDER, 100);
        Perk theme = createTestPerk("Dark Theme", PerkType.THEME, 200);
        Perk title = createTestPerk("Epic Gamer", PerkType.TITLE, 150);
        purchaseAndEquipPerk(user, border);
        purchaseAndEquipPerk(user, theme);
        purchaseAndEquipPerk(user, title);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + theme.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(border.getId());
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isNull();
        assertThat(updatedUser.getCosmetics().getEquippedTitleId()).isEqualTo(title.getId());
    }

    @Test
    void unequipPerk_shouldAllowReEquipping_afterUnequip() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);
        purchaseAndEquipPerk(user, perk);

        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Act
        mockMvc.perform(post("/api/platform/perks/" + perk.getId().id() + "/equip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isEqualTo(perk.getId());
    }

    @Test
    void unequipPerk_shouldUnequipAllPerkTypes() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk border = createTestPerk("Cool Border", PerkType.BORDER, 100);
        Perk theme = createTestPerk("Dark Theme", PerkType.THEME, 200);
        Perk title = createTestPerk("Epic Gamer", PerkType.TITLE, 150);
        purchaseAndEquipPerk(user, border);
        purchaseAndEquipPerk(user, theme);
        purchaseAndEquipPerk(user, title);

        // Act
        mockMvc.perform(post("/api/platform/perks/" + border.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/" + theme.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/" + title.getId().id() + "/unequip")
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCosmetics().getEquippedBorderId()).isNull();
        assertThat(updatedUser.getCosmetics().getEquippedThemeId()).isNull();
        assertThat(updatedUser.getCosmetics().getEquippedTitleId()).isNull();
        assertThat(updatedUser.hasPerk(border.getId())).isTrue();
        assertThat(updatedUser.hasPerk(theme.getId())).isTrue();
        assertThat(updatedUser.hasPerk(title.getId())).isTrue();
    }
}