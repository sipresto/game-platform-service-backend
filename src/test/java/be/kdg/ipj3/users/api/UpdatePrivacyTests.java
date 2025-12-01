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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
    void updatePrivacySettings_shouldUpdateSuccessfully_whenValidVisibilityAndUserExists() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettings(any(), any());
    }

    @Test
    void updatePrivacySettings_shouldUpdateSuccessfully_whenVisibilityIsPrivate() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "PRIVATE";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettings(any(), any());
    }

    @Test
    void updatePrivacySettings_shouldUpdateSuccessfully_whenVisibilityIsPublic() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "PUBLIC";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isOk());

        then(userService).should().updatePrivacySettings(any(), any());
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String invalidVisibility = "INVALID_VISIBILITY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + invalidVisibility + "\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenEmptyVisibility() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacySettings_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePrivacySettings_shouldReturnForbidden_whenWrongRole() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePrivacySettings_shouldReturnForbidden_whenNoRole() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePrivacySettings_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        String visibility = "FRIENDS_ONLY";

        // Mock the service to throw NotFoundException when user doesn't exist
        doThrow(new be.kdg.ipj3.shared.domain.NotFoundException("User not found"))
                .when(userService).updatePrivacySettings(any(), any());

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", nonExistentUserId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePrivacySettings_shouldHandleInvalidUUIDFormat() throws Exception {
        // Arrange
        String invalidUserId = "not-a-uuid";
        String visibility = "FRIENDS_ONLY";

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", invalidUserId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + visibility + "\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenMissingRequestBody() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrivacySettings_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/api/user/{id}/privacy", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("test-keycloak-id")
                                        .claim("preferred_username", "testuser"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }
}