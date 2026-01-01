package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.domain.VisibleField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateFieldVisibilityIntegrationTests extends BaseIntegrationTest {

    @Test
    void updateFieldVisibility_shouldSucceed_whenValidFieldAndVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt(keycloakId, username))
                        .param("field", "PROFILE")
                        .param("visibility", "PRIVATE"))
                .andExpect(status().isOk());

        // Assert
        var user = userRepository.findByKeycloakId(keycloakId).orElseThrow();
        assertThat(user.getVisibilitySettings().getVisibility(VisibleField.PROFILE)).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    void updateFieldVisibility_shouldReturnBadRequest_whenInvalidField() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt(keycloakId, username))
                        .param("visibility", "PRIVATE")
                        .param("field", "INVALID_FIELD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFieldVisibility_shouldReturnBadRequest_whenInvalidVisibility() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createTestUser(keycloakId, username, "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/privacy/fields")
                        .with(validJwt(keycloakId, username))
                        .param("visibility", "INVALID_VISIBILITY")
                        .param("field", "PROFILE"))
                .andExpect(status().isBadRequest());
    }
}