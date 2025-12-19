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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class SendFriendRequestIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JpaUserEntity createUser(String keycloakId, String username) {
        return userRepository.save(new JpaUserEntity(
                UUID.randomUUID(), keycloakId, username,
                new JpaProfile(username, "Bio", null, null), 0, Visibility.PUBLIC, "Test@gmail.com"
        ));
    }

    private JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt().jwt(b -> b.subject(keycloakId).claim("preferred_username", "user"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Test
    @Transactional
    void sendFriendRequest_ShouldSucceed_WhenUsersExist() throws Exception {
        // Arrange
        JpaUserEntity sender = createUser("kc-sender", "sender");
        JpaUserEntity receiver = createUser("kc-receiver", "receiver");

        // Act
        mockMvc.perform(post("/api/user/{receiverId}/send-friend-request", receiver.getId())
                        .with(jwtForUser(sender.getKeycloakId())))
                .andExpect(status().isNoContent());

        // Assert
        JpaUserEntity updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();
        assertThat(updatedReceiver.toDomain().getFriendRequests())
                .hasSize(1)
                .extracting(req -> req.sender().id())
                .contains(sender.getId());
    }

    @Test
    void sendFriendRequest_ShouldFail_WhenReceiverNotFound() throws Exception {
        // Arrange
        JpaUserEntity sender = createUser("kc-sender", "sender");

        // Act & Assert
        mockMvc.perform(post("/api/user/{receiverId}/send-friend-request", UUID.randomUUID())
                        .with(jwtForUser(sender.getKeycloakId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendFriendRequest_ShouldFail_WhenUnauthorized() throws Exception {
        JpaUserEntity receiver = createUser("kc-receiver", "receiver");

        mockMvc.perform(post("/api/user/{receiverId}/send-friend-request", receiver.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void sendFriendRequest_ShouldFail_WhenSendingToSelf() throws Exception {
        // Arrange
        JpaUserEntity user = createUser("kc-user", "user");

        // Act & Assert
        mockMvc.perform(post("/api/user/{receiverId}/send-friend-request", user.getId())
                        .with(jwtForUser(user.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }
}