package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import be.kdg.ipj3.users.shared.UserId;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UpdatePrivacySettingsIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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

        User userEntity = new User(
                new UserId(userId),
                keycloakId,
                username,
                List.of(),
                List.of(),
                0,
                new Profile(username, "Test bio", null, null),
                VisibilitySettings.defaultPublic(),
                "Test@gmail.com"
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

        assertThat(settings.getVisibility(VisibleField.PROFILE)).isEqualTo(Visibility.PUBLIC);
        assertThat(settings.getVisibility(VisibleField.POINTS)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.FAVORITE_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.OWNED_GAMES)).isEqualTo(Visibility.FRIENDS_ONLY);
        assertThat(settings.getVisibility(VisibleField.FRIENDS_LIST)).isEqualTo(Visibility.FRIENDS_ONLY);
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
