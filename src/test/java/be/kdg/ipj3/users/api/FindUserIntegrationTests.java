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
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class FindUserIntegrationTests {

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

    private UUID createUser(String keycloakId, String username) {
        UUID userId = UUID.randomUUID();
        JpaUserEntity userEntity = new JpaUserEntity(
                userId,
                keycloakId,
                username,
                new JpaProfile(username, "Test bio"),
                Visibility.PUBLIC
        );
        userRepository.save(userEntity);
        return userId;
    }

    @Test
    void findUser_shouldReturnUser_whenAuthenticated() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        UUID userId = createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(get("/api/user")
                        .with(validJwt(keycloakId, username))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.profile.displayName").value(username))
                .andExpect(jsonPath("$.profile.bio").value("Test bio"));
    }

    @Test
    void findUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findUser_shouldReturnForbidden_whenWrongRole() throws Exception {
        String keycloakId = "test-keycloak-id";
        String username = "testuser";
        createUser(keycloakId, username);

        // Act & Assert
        mockMvc.perform(get("/api/user")
                        .with(jwt()
                                .jwt(builder -> builder
                                        .subject(keycloakId)
                                        .claim("preferred_username", username))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
