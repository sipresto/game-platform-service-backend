package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.VisibilitySettings;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetBlockedUsersTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt().jwt(b -> b.subject(keycloakId).claim("preferred_username", "user"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private User createUser(String keycloakId, String username) {
        User user = new User(
                new UserId(UUID.randomUUID()), keycloakId, username, List.of(), List.of(),
                0, new Profile(username, "bio", null, null),
                VisibilitySettings.defaultPublic(), "Test@gmail.com"
        );
        userRepository.save(user);
        return user;
    }

    @Test
    void getBlockedUsers_shouldReturnFilledList_whenBlockedUsersExist() throws Exception {
        User user1 = createUser("user1", "user1");
        User user2 = createUser("user2", "user2");

        user1.block(user2.getId());
        userRepository.save(user1);

        mockMvc.perform(get("/api/platform/users/blocked")
                .with(jwtForUser(user1.getKeycloakId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user2.getId().id().toString()))
                .andExpect(jsonPath("$[0].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[0].profile.bio").value(user2.getProfile().bio()));
    }

    @Test
    void getBlockedUsers_shouldReturnEmptyList_whenNoBlockedUsersExist() throws Exception {
        User user1 = createUser("user1", "user1");

        mockMvc.perform(get("/api/platform/users/blocked")
                        .with(jwtForUser(user1.getKeycloakId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getBlockedUsers_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        User user1 = createUser("user1", "user1");

        mockMvc.perform(get("/api/platform/users/blocked"))
                .andExpect(status().isUnauthorized());
    }
}
