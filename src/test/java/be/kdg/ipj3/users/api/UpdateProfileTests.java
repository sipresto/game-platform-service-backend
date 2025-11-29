package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.api.dto.UserDto;
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

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class UpdateProfileTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private be.kdg.ipj3.catalog.application.GameService gameService;

    @Test
    void updateProfile_shouldUpdateSuccessfully_whenValidData() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto(
                "Updated Display Name",
                "Updated bio"
        );

        // Act & Assert
        mockMvc.perform(put("/api/user/profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Username updated successfully"));

        then(userService).should().updateProfile(keycloakId, "Updated Display Name", "Updated bio");
    }

    @Test
    void updateProfile_shouldReturnBadRequest_whenBlankDisplayName() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("", "Updated bio");

        // Act & Assert
        mockMvc.perform(put("/api/user/profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        // Act & Assert
        mockMvc.perform(put("/api/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_shouldReturnForbidden_whenWrongRole() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        UserDto.ProfileDto profileDto = new UserDto.ProfileDto("Display Name", "Bio");

        // Act & Assert
        mockMvc.perform(put("/api/user/profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject(keycloakId)
                                        .claim("preferred_username", keycloakUsername))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isForbidden());
    }
}