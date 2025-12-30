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
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@TestPropertySource("classpath:application-test.properties")
//public class FindAllPublicUsersIntegrationTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private JpaUserRepository userRepository;
//
//    private JwtRequestPostProcessor validJwt(String subject, String preferredUsername) {
//        return jwt()
//                .jwt(builder -> builder
//                        .subject(subject)
//                        .claim("preferred_username", preferredUsername))
//                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
//    }
//
//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll();
//    }
//
//    private void createTestUsers() {
//        Map<VisibleField, Visibility> publicVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
//        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
//        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
//        privateVisibility.put(VisibleField.POINTS, Visibility.PRIVATE);
//        privateVisibility.put(VisibleField.FAVORITE_GAMES, Visibility.PRIVATE);
//        privateVisibility.put(VisibleField.OWNED_GAMES, Visibility.PRIVATE);
//        privateVisibility.put(VisibleField.FRIENDS_LIST, Visibility.PRIVATE);
//
//        JpaUserEntity publicUser = new JpaUserEntity(
//                UUID.randomUUID(),
//                "kc-public",
//                "public-user",
//                new JpaProfile("Public", "I am public", null, null),
//                100,
//                "public@gmail.com",
//                publicVisibility
//        );
//
//        JpaUserEntity privateUser = new JpaUserEntity(
//                UUID.randomUUID(),
//                "kc-private",
//                "private-user",
//                new JpaProfile("Private", "I am private", null, null),
//                50,
//                "private@gmail.com",
//                privateVisibility
//        );
//
//        userRepository.save(publicUser);
//        userRepository.save(privateUser);
//    }
//
//    @Test
//    void getAllUsers_shouldReturnAllUsersWithRespectiveVisibility_whenBothExist() throws Exception {
//        // Arrange
//        createTestUsers();
//
//        // Act & Assert
//        mockMvc.perform(get("/api/platform/users").with(validJwt("kc-public", "public-user")))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[?(@.username=='public-user')].profile.bio").value("I am public"))
//                .andExpect(jsonPath("$[?(@.username=='public-user')].points").value(100))
//                .andExpect(jsonPath("$[?(@.username=='private-user')].profile.bio").doesNotExist())
//                .andExpect(jsonPath("$[?(@.username=='private-user')].points").doesNotExist());
//    }
//
//    @Test
//    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() throws Exception {
//        mockMvc.perform(get("/api/platform/users").with(validJwt("kc-public", "public-user")))
//                .andExpect(status().isNotFound());
//    }
//}