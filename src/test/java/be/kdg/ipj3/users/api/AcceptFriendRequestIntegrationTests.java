package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class AcceptFriendRequestIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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

    private JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt().jwt(b -> b.subject(keycloakId).claim("preferred_username", "user"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Test
    void acceptFriendRequest_ShouldSucceed_WhenRequestExists() throws Exception {
        // Arrange
        User sender = createUser("kc-otherUser", "otherUser");
        User receiver = createUser("kc-receiver", "receiver");

        receiver.addIncomingFriendRequest(new FriendRequest(sender.getId()));
        userRepository.save(receiver);

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", sender.getId().id())
                        .with(jwtForUser(receiver.getKeycloakId())))
                .andExpect(status().isNoContent());

        // Assert
        User updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();
        User updatedSender = userRepository.findById(sender.getId()).orElseThrow();

        assertThat(updatedReceiver.getFriends()).contains(sender.getId());
        assertThat(updatedSender.getFriends()).contains(receiver.getId());

        assertThat(updatedReceiver.getIncomingFriendRequests()).isEmpty();
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenNoRequestExists() throws Exception {
        // Arrange
        User sender = createUser("kc-otherUser", "otherUser");
        User receiver = createUser("kc-receiver", "receiver");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", sender.getId().id())
                        .with(jwtForUser(receiver.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenSenderDoesNotExist() throws Exception {
        // Arrange
        User receiver = createUser("kc-receiver", "receiver");

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", UUID.randomUUID())
                        .with(jwtForUser(receiver.getKeycloakId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenUnauthorized() throws Exception {
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenAcceptingOwnRequest() throws Exception {
        // Arrange
        User user = createUser("kc-user", "user");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", user.getId().id())
                        .with(jwtForUser(user.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }
}