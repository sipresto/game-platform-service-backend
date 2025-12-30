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
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@TestPropertySource("classpath:application-test.properties")
//@Transactional
//public class GetVisibilitySettingsIntegrationTests {
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
//    private void createUser(String keycloakId, String username) {
//        UUID userId = UUID.randomUUID();
//        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();
//
//        JpaUserEntity userEntity = new JpaUserEntity(
//                userId,
//                keycloakId,
//                username,
//                new JpaProfile(username, "Test bio", null, null),
//                0,
//                "Test@gmail.com",
//                visibility
//        );
//        userRepository.save(userEntity);
//    }
//
//    @Test
//    void getVisibilitySettings_shouldReturnSettings_whenAuthenticated() throws Exception {
//        String keycloakId = "test-keycloak-id";
//        String username = "testuser";
//        createUser(keycloakId, username);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users/privacy/settings")
//                        .with(validJwt(keycloakId, username)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fieldVisibilities.PROFILE").value("PUBLIC"))
//                .andExpect(jsonPath("$.fieldVisibilities.POINTS").value("PUBLIC"))
//                .andExpect(jsonPath("$.fieldVisibilities.FAVORITE_GAMES").value("PUBLIC"))
//                .andExpect(jsonPath("$.fieldVisibilities.OWNED_GAMES").value("FRIENDS_ONLY"))
//                .andExpect(jsonPath("$.fieldVisibilities.FRIENDS_LIST").value("FRIENDS_ONLY"));
//    }
//
//    @Test
//    void getVisibilitySettings_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
//        mockMvc.perform(get("/api/platform/users/privacy/settings"))
//                .andExpect(status().isUnauthorized());
//    }
//}
