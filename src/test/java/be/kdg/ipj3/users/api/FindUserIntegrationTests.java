//package be.kdg.ipj3.users.api;
//
//import be.kdg.ipj3.users.domain.Visibility;
//import be.kdg.ipj3.users.domain.VisibleField;
//import be.kdg.ipj3.users.domain.VisibilitySettings;
//import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
//import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
//import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Map;
//import java.util.UUID;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@TestPropertySource("classpath:application-test.properties")
//public class FindUserIntegrationTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private JpaUserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll();
//    }
//
//    private JwtRequestPostProcessor validJwt(String subject, String username) {
//        return jwt()
//                .jwt(builder -> builder
//                        .subject(subject)
//                        .claim("preferred_username", username))
//                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
//    }
//
//    private UUID createUser(String keycloakId, String username) {
//        UUID userId = UUID.randomUUID();
//        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();
//
//        JpaUserEntity userEntity = new JpaUserEntity(
//                userId,
//                keycloakId,
//                username,
//                new JpaProfile(username, "Test bio", null, null),
//                100,
//                "Test@gmail.com",
//                visibility
//        );
//        userRepository.save(userEntity);
//        return userId;
//    }
//
//    @Test
//    void findUser_shouldReturnUser_whenAuthenticated() throws Exception {
//        // Arrange
//        String keycloakId = "test-keycloak-id";
//        String username = "testuser";
//        UUID userId = createUser(keycloakId, username);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/me")
//                        .with(validJwt(keycloakId, username))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(userId.toString()))
//                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
//                .andExpect(jsonPath("$.username").value(username))
//                .andExpect(jsonPath("$.profile.displayName").value(username))
//                .andExpect(jsonPath("$.profile.bio").value("Test bio"));
//    }
//
//    @Test
//    void findUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/me")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void findUser_shouldReturnForbidden_whenWrongRole() throws Exception {
//        String keycloakId = "test-keycloak-id";
//        String username = "testuser";
//        createUser(keycloakId, username);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/me")
//                        .with(jwt()
//                                .jwt(builder -> builder
//                                        .subject(keycloakId)
//                                        .claim("preferred_username", username))
//                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void getUserById_shouldRespectVisibility_whenViewerIsNotOwner() throws Exception {
//        // Arrange
//        UUID friendId = UUID.randomUUID();
//        String friendKeycloakId = "friend-keycloak";
//        String keycloakId = "test-keycloak-id";
//        String username = "testuser";
//        UUID userId = createUser(keycloakId, username);
//
//        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
//        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
//        privateVisibility.put(VisibleField.POINTS, Visibility.PRIVATE);
//
//        JpaUserEntity friend = new JpaUserEntity(
//                friendId,
//                friendKeycloakId,
//                "friend",
//                new JpaProfile("Friend", "Private bio", null, null),
//                200,
//                "friend@example.com",
//                privateVisibility
//        );
//        userRepository.save(friend);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/{userId}", friendId)
//                        .with(validJwt(keycloakId, username)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("friend"))
//                .andExpect(jsonPath("$.profile.bio").doesNotExist())
//                .andExpect(jsonPath("$.points").doesNotExist());
//    }
//
//    @Test
//    void getUserById_shouldShowAllData_whenViewerIsOwner() throws Exception {
//        String keycloakId = "test-keycloak-id";
//        String username = "testuser";
//        UUID userId = createUser(keycloakId, username);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/{userId}", userId)
//                        .with(validJwt(keycloakId, username)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("testuser"))
//                .andExpect(jsonPath("$.profile.bio").value("Test bio"))
//                .andExpect(jsonPath("$.points").value(100));
//    }
//}