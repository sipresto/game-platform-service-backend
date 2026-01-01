package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetVisibilitySettingsIntegrationTests extends BaseIntegrationTest {

    @Test
    void getVisibilitySettings_shouldReturnSettings_whenAuthenticated() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/privacy/settings")
                        .with(validJwt(keycloakId, username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldVisibilities.PROFILE").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.POINTS").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.FAVORITE_GAMES").value("PUBLIC"))
                .andExpect(jsonPath("$.fieldVisibilities.OWNED_GAMES").value("FRIENDS_ONLY"))
                .andExpect(jsonPath("$.fieldVisibilities.FRIENDS_LIST").value("FRIENDS_ONLY"));
    }

    @Test
    void getVisibilitySettings_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/users/privacy/settings"))
                .andExpect(status().isUnauthorized());
    }
}