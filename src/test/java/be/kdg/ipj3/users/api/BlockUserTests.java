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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class BlockUserTests {

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
    void block_shouldSucceed_whenUsersAreNotFriends() throws Exception {
        User user1 = createUser("kc-user1", "User1");
        User user2 = createUser("kc-user2", "User2");

        mockMvc.perform(patch("/api/platform/users/{blockId}/block", user2.getId().id())
                        .with(jwtForUser(user1.getKeycloakId())))
                .andExpect(status().isNoContent());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getBlockedUsers().size()).isEqualTo(1);
        assertThat(updatedUser2.getBlockedUsers().size()).isEqualTo(0);
    }

    @Test
    void block_shouldSucceed_whenUsersAreFriends() throws Exception {
        User user1 = createUser("kc-user1", "User1");
        User user2 = createUser("kc-user2", "User2");

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());

        userRepository.save(user1);
        userRepository.save(user2);

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user2.getId().id())
                        .with(jwtForUser(user1.getKeycloakId())))
                .andExpect(status().isNoContent());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getFriends().size()).isEqualTo(0);
        assertThat(updatedUser2.getFriends().size()).isEqualTo(0);

        assertThat(updatedUser1.getBlockedUsers().size()).isEqualTo(1);
    }

    @Test
    void block_shouldFail_whenIdsAreTheSame() throws Exception {
        User user1 = createUser("kc-user1", "User1");

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user1.getId().id())
                        .with(jwtForUser(user1.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void block_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        User user1 = createUser("kc-user1", "User1");

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user1.getId().id()))
                .andExpect(status().isUnauthorized());
    }
}
