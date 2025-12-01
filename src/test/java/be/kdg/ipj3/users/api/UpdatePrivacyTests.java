package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.application.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class UpdatePrivacyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private be.kdg.ipj3.catalog.application.GameService gameService;

    @Test
    void updatePrivacySettings_shouldUpdateSuccessfully_whenValidVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettingsByKeycloakId(
                eq(be.kdg.ipj3.users.domain.Visibility.FRIENDS_ONLY),
                eq(keycloakId)
        );
    }

    @Test
    void updatePrivacySettings_shouldUpdateSuccessfully_whenVisibilityIsPrivate() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "PRIVATE";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettingsByKeycloakId(
                eq(be.kdg.ipj3.users.domain.Visibility.PRIVATE),
                eq(keycloakId)
        );
    }

    @Test
    void updatePrivacySettings_shouldUpdateSuccessfully_whenVisibilityIsPublic() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "PUBLIC";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettingsByKeycloakId(
                eq(be.kdg.ipj3.users.domain.Visibility.PUBLIC),
                eq(keycloakId)
        );
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String invalidVisibility = "INVALID_VISIBILITY";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + invalidVisibility + "\""))
                .andExpect(status().isBadRequest());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenEmptyVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"\""))
                .andExpect(status().isBadRequest());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isUnauthorized());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnForbidden_whenWrongRole() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnForbidden_whenNoRole() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange
        String keycloakId = "non-existent-keycloak-id";
        String keycloakUsername = "testuser";
        String visibility = "FRIENDS_ONLY";

        // Mock the service to throw NotFoundException when user doesn't exist
        doThrow(new be.kdg.ipj3.shared.domain.NotFoundException("User with keycloak id [" + keycloakId + "] not found"))
                .when(userService).updatePrivacySettingsByKeycloakId(
                        eq(be.kdg.ipj3.users.domain.Visibility.FRIENDS_ONLY),
                        eq(keycloakId)
                );

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenMissingRequestBody() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        // Act & Assert
        mockMvc.perform(put("/api/user/privacy")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());

        then(userService).shouldHaveNoInteractions();
    }
}