package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.*;
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

    @Autowired private MockMvc mockMvc;
    @Autowired private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JpaUserEntity createUser(String keycloakId, String username) {
        Map<VisibleField, Visibility> visibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        return userRepository.save(new JpaUserEntity(
                UUID.randomUUID(), keycloakId, username,
                new JpaProfile(username, "Bio", null, null), 0, "Test@gmail.com", visibility
        ));
    }

    private JwtRequestPostProcessor jwtForUser(String keycloakId) {
        return jwt().jwt(b -> b.subject(keycloakId).claim("preferred_username", "user"))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Test
    @Transactional
    void acceptFriendRequest_ShouldSucceed_WhenRequestExists() throws Exception {
        // Arrange
        JpaUserEntity senderEntity = createUser("kc-otherUser", "otherUser");
        JpaUserEntity receiverEntity = createUser("kc-receiver", "receiver");

        User receiverDomain = receiverEntity.toDomain();
        User senderDomain = senderEntity.toDomain();

        receiverDomain.addIncomingFriendRequest(new FriendRequest(senderDomain.getId()));
        userRepository.save(JpaUserEntity.fromDomain(receiverDomain));

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", senderEntity.getId())
                        .with(jwtForUser(receiverEntity.getKeycloakId())))
                .andExpect(status().isNoContent());

        // Assert
        JpaUserEntity updatedReceiver = userRepository.findById(receiverEntity.getId()).orElseThrow();
        JpaUserEntity updatedSender = userRepository.findById(senderEntity.getId()).orElseThrow();

        assertThat(updatedReceiver.getFriends()).contains(senderEntity.getId());
        assertThat(updatedSender.getFriends()).contains(receiverEntity.getId());

        assertThat(updatedReceiver.toDomain().getIncomingFriendRequests()).isEmpty();
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenNoRequestExists() throws Exception {
        // Arrange
        JpaUserEntity sender = createUser("kc-otherUser", "otherUser");
        JpaUserEntity receiver = createUser("kc-receiver", "receiver");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", sender.getId())
                        .with(jwtForUser(receiver.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenSenderDoesNotExist() throws Exception {
        // Arrange
        JpaUserEntity receiver = createUser("kc-receiver", "receiver");

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
    @Transactional
    void acceptFriendRequest_ShouldFail_WhenAcceptingOwnRequest() throws Exception {
        // Arrange
        JpaUserEntity user = createUser("kc-user", "user");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", user.getId())
                        .with(jwtForUser(user.getKeycloakId())))
                .andExpect(status().isBadRequest());
    }
}