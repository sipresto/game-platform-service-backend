package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Visibility;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UpdatePrivacyIntegrationTests {

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

    private String createUser(String keycloakId, String username) {
        UUID userId = UUID.randomUUID();
        JpaUserEntity user = new JpaUserEntity(
                userId,
                keycloakId,
                username,
                new JpaProfile(username, "Test bio", null, null),
                0,
                Visibility.PUBLIC,
                "Test@gmail.com"
        );
        userRepository.save(user);
        return keycloakId;
    }

    @Test
    void updatePrivacy_shouldSucceed_whenValidVisibility() throws Exception {
        // Arrange
        String keycloakId = createUser("kc-id", "testuser");
        String visibility = "FRIENDS_ONLY";

        // Act
        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        Optional<JpaUserEntity> opt = userRepository.findByKeycloakId(keycloakId);
        JpaUserEntity updated = opt.orElseThrow(() -> new AssertionError("User not found in database after update"));

        assertThat(updated.getVisibility())
                .isEqualTo(Visibility.valueOf(visibility));
    }

    @Test
    void updatePrivacy_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        String keycloakId = createUser("kc-id2", "testuser");
        String invalidVisibility = "INVALID_VISIBILITY";

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + invalidVisibility + "\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacy_shouldReturnBadRequest_whenEmptyVisibility() throws Exception {
        String keycloakId = createUser("kc-id3", "testuser");

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacy_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        String visibility = "FRIENDS_ONLY";

        mockMvc.perform(put("/api/platform/users/privacy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePrivacy_shouldReturnForbidden_whenWrongRole() throws Exception {
        String keycloakId = createUser("kc-id4", "testuser");
        String visibility = "FRIENDS_ONLY";

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(keycloakId)
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePrivacy_shouldReturnForbidden_whenNoRole() throws Exception {
        String keycloakId = createUser("kc-id5", "testuser");
        String visibility = "FRIENDS_ONLY";

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(keycloakId)
                                        .claim("preferred_username", "testuser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePrivacy_shouldReturnBadRequest_whenMissingRequestBody() throws Exception {
        String keycloakId = createUser("kc-id6", "testuser");

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacy_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
        String keycloakId = createUser("kc-id7", "testuser");

        mockMvc.perform(put("/api/platform/users/privacy")
                        .with(validJwt(keycloakId, "testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }
}
