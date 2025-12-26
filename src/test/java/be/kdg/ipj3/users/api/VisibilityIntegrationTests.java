package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.domain.VisibleField;
import be.kdg.ipj3.users.domain.VisibilitySettings;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Transactional
public class VisibilityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    private String testKeycloakId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUserId = UUID.randomUUID();
        testKeycloakId = "test-keycloak-id-" + UUID.randomUUID();

        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();

        JpaUserEntity user = new JpaUserEntity(
                testUserId,
                testKeycloakId,
                "testuser",
                new JpaProfile("Test User", "Test bio", null, null),
                100,
                "test@example.com",
                visibility
        );
        userRepository.save(user);
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt()
                .jwt(builder -> builder
                        .subject(testKeycloakId)
                        .claim("preferred_username", "testuser"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Test
    void updatePrivacyTemplate_shouldSucceed_whenValidVisibility() throws Exception {
        // Act
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"FRIENDS_ONLY\""))
                .andExpect(status().isOk());

        // Assert
        var user = userRepository.findByKeycloakId(testKeycloakId).orElseThrow();
        var settings = user.getVisibilitySettings();

        assertThat(settings.get(VisibleField.PROFILE)).isEqualTo(Visibility.PUBLIC);
        assertThat(settings.get(VisibleField.POINTS)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.FAVORITE_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.OWNED_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.FRIENDS_LIST)).isEqualTo(Visibility.FRIENDS_ONLY);
    }

    @Test
    void updateFieldVisibility_shouldSucceed_whenValidFieldAndVisibility() throws Exception {
        // Act
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt())
                        .param("field", "PROFILE")
                        .param("visibility", "PRIVATE"))
                .andExpect(status().isOk());

        // Assert
        var user = userRepository.findByKeycloakId(testKeycloakId).orElseThrow();
        assertThat(user.getVisibilitySettings().get(VisibleField.PROFILE)).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    void getVisibilitySettings_shouldReturnSettings_whenAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/users/privacy/settings")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldVisibilities.PROFILE").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.POINTS").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.FAVORITE_GAMES").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.OWNED_GAMES").value("FRIENDS_ONLY"))
                .andExpect(jsonPath("$.fieldVisibilities.FRIENDS_LIST").value("FRIENDS_ONLY"));
    }

    @Test
    void getUserById_shouldRespectVisibility_whenViewerIsNotOwner() throws Exception {
        // Arrange
        UUID friendId = UUID.randomUUID();
        String friendKeycloakId = "friend-keycloak";

        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.POINTS, Visibility.PRIVATE);

        JpaUserEntity friend = new JpaUserEntity(
                friendId,
                friendKeycloakId,
                "friend",
                new JpaProfile("Friend", "Private bio", null, null),
                200,
                "friend@example.com",
                privateVisibility
        );
        userRepository.save(friend);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/{userId}", friendId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("friend"))
                .andExpect(jsonPath("$.profile.bio").doesNotExist())
                .andExpect(jsonPath("$.points").doesNotExist());
    }

    @Test
    void getUserById_shouldShowAllData_whenViewerIsOwner() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/users/{userId}", testUserId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.profile.bio").value("Test bio"))
                .andExpect(jsonPath("$.points").value(100));
    }

    @Test
    void updateFieldVisibility_shouldReturnBadRequest_whenInvalidField() throws Exception {
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt())
                        .param("visibility", "PRIVATE")
                        .param("field", "INVALID_FIELD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFieldVisibility_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt())
                        .param("visibility", "INVALID_VISIBILITY")
                        .param("field", "PROFILE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacyTemplate_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"INVALID_VISIBILITY\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVisibilitySettings_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/platform/users/privacy/settings"))
                .andExpect(status().isUnauthorized());
    }
}