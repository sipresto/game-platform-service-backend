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
public class UpdatePrivacySettingsIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt(String subject, String username) {
        return jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", username))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private void createUser(String keycloakId, String username) {
        UUID userId = UUID.randomUUID();
        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();

        JpaUserEntity userEntity = new JpaUserEntity(
                userId,
                keycloakId,
                username,
                new JpaProfile(username, "Test bio", null, null),
                0,
                "Test@gmail.com",
                visibility
        );
        userRepository.save(userEntity);
    }

    @Test
    void updatePrivacyTemplate_shouldSucceed_whenValidVisibility() throws Exception {
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createUser(keycloakId, username);

        // Act
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"FRIENDS_ONLY\""))
                .andExpect(status().isOk());

        // Assert
        var user = userRepository.findByKeycloakId(keycloakId).orElseThrow();
        var settings = user.getVisibilitySettings();

        assertThat(settings.get(VisibleField.PROFILE)).isEqualTo(Visibility.PUBLIC);
        assertThat(settings.get(VisibleField.POINTS)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.FAVORITE_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.OWNED_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.get(VisibleField.FRIENDS_LIST)).isEqualTo(Visibility.FRIENDS_ONLY);
    }

    @Test
    void updatePrivacyTemplate_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"INVALID_VISIBILITY\""))
                .andExpect(status().isBadRequest());
    }
}
