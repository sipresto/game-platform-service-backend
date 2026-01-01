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
public class PurchasePerkTests {

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
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt2() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id-2")
                .claim("preferred_username", "test-user-2"));
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

    @Test
    void purchasePerk_shouldDeductPoints_whenUserHasEnoughPoints() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);

        // Act
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(400);
        assertThat(updatedUser.hasPerk(perk.getId())).isTrue();
    }

    @Test
    void purchasePerk_shouldReturnBadRequest_whenUserHasInsufficientPoints() throws Exception {
        // Arrange
        createTestUser("keycloak-id", "test-user", "test@gmail.com", 50);
        Perk perk = createTestPerk("Expensive Theme", PerkType.THEME, 100);

        // Act & Assert
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void purchasePerk_shouldReturnBadRequest_whenPerkAlreadyOwned() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        Perk perk = createTestPerk("Cool Border", PerkType.BORDER, 100);

        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(400);
    }

    @Test
    void purchasePerk_shouldReturnNotFound_whenPerkDoesNotExist() throws Exception {
        // Arrange
        createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);

        // Act & Assert
        mockMvc.perform(post("/api/platform/perks/purchase/" + UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void purchasePerk_shouldAllowPurchase_whenUserHasExactPoints() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 100);
        Perk perk = createTestPerk("Affordable Border", PerkType.BORDER, 100);

        // Act
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(0);
        assertThat(updatedUser.hasPerk(perk.getId())).isTrue();
    }

    @Test
    void purchasePerk_shouldAllowDifferentUsers_toPurchaseSamePerk() throws Exception {
        // Arrange
        User user1 = createTestUser("keycloak-id", "test-user", "test@gmail.com", 500);
        User user2 = createTestUser("keycloak-id-2", "test-user-2", "test2@gmail.com", 500);
        Perk perk = createTestPerk("Popular Border", PerkType.BORDER, 100);

        // Act
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/purchase/" + perk.getId().id())
                        .with(validJwt2()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();
        assertThat(updatedUser1.hasPerk(perk.getId())).isTrue();
        assertThat(updatedUser2.hasPerk(perk.getId())).isTrue();
        assertThat(updatedUser1.getPoints()).isEqualTo(400);
        assertThat(updatedUser2.getPoints()).isEqualTo(400);
    }

    @Test
    void purchasePerk_shouldAllowPurchase_ofMultipleDifferentPerks() throws Exception {
        // Arrange
        User user = createTestUser("keycloak-id", "test-user", "test@gmail.com", 1000);
        Perk perk1 = createTestPerk("Border 1", PerkType.BORDER, 100);
        Perk perk2 = createTestPerk("Theme 1", PerkType.THEME, 200);
        Perk perk3 = createTestPerk("Title 1", PerkType.TITLE, 150);

        // Act
        mockMvc.perform(post("/api/platform/perks/purchase/" + perk1.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/purchase/" + perk2.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/platform/perks/purchase/" + perk3.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getPoints()).isEqualTo(550);
        assertThat(updatedUser.hasPerk(perk1.getId())).isTrue();
        assertThat(updatedUser.hasPerk(perk2.getId())).isTrue();
        assertThat(updatedUser.hasPerk(perk3.getId())).isTrue();
    }
}