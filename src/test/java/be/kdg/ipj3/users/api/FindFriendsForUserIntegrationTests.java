package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.domain.VisibleField;
import be.kdg.ipj3.users.domain.VisibilitySettings;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class FindFriendsForUserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt(String subject, String preferredUsername) {
        return jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", preferredUsername))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private UUID createUserWithFriend(UUID friendId) {
        UUID mainId = UUID.randomUUID();

        Map<VisibleField, Visibility> mainVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        Map<VisibleField, Visibility> friendVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();

        JpaUserEntity mainUser = new JpaUserEntity(
                mainId,
                "kc-main-" + mainId,
                "main-user-" + mainId,
                new JpaProfile("Main", "main bio", null, null),
                100,
                "main@gmail.com",
                mainVisibility
        );

        JpaUserEntity friend = new JpaUserEntity(
                friendId,
                "kc-friend-" + friendId,
                "friend-user-" + friendId,
                new JpaProfile("Friend", "friend bio",null, null),
                50,
                "friend@gmail.com",
                friendVisibility
        );

        mainUser.getFriends().add(friendId);

        userRepository.save(friend);
        userRepository.save(mainUser);

        return mainId;
    }

    @Test
    void getFriends_shouldReturnFriends_forAuthenticatedUser() throws Exception {
        // Arrange
        UUID friendId = UUID.randomUUID();
        UUID mainId = createUserWithFriend(friendId);
        String mainKeycloakId = "kc-main-" + mainId;

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/friends")
                        .with(validJwt(mainKeycloakId, "main-user")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("friend-user-" + friendId))
                .andExpect(jsonPath("$[0].id").value(friendId.toString()))
                .andExpect(jsonPath("$[0].profile.bio").value("friend bio"))
                .andExpect(jsonPath("$[0].points").value(50));
    }

    @Test
    void getFriends_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        UUID friendId = UUID.randomUUID();
        createUserWithFriend(friendId);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/friends"))
                .andExpect(status().isUnauthorized());
    }
}