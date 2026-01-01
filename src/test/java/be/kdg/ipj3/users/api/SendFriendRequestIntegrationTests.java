package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SendFriendRequestIntegrationTests extends BaseIntegrationTest {

    @Test
    void sendFriendRequest_ShouldSucceed_WhenUsersExist() throws Exception {
        // Arrange
        User sender = createTestUser("kc-otherUser", "otherUser", "other@gmail.com");
        User receiver = createTestUser("kc-receiver", "receiver", "receiver@gmail.com");

        // Act
        mockMvc.perform(post("/api/platform/users/{receiverId}/send-friend-request", receiver.getId().id())
                        .with(validJwt(sender.getKeycloakId(), "otherUser")))
                .andExpect(status().isNoContent());

        // Assert
        User updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();
        assertThat(updatedReceiver.getIncomingFriendRequests())
                .hasSize(1)
                .extracting(req -> req.otherUser().id())
                .contains(sender.getId().id());
    }

    @Test
    void sendFriendRequest_ShouldFail_WhenReceiverNotFound() throws Exception {
        // Arrange
        User sender = createTestUser("kc-otherUser", "otherUser", "other@gmail.com");

        // Act & Assert
        mockMvc.perform(post("/api/platform/users/{receiverId}/send-friend-request", UUID.randomUUID())
                        .with(validJwt(sender.getKeycloakId(), "otherUser")))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendFriendRequest_ShouldFail_WhenUnauthorized() throws Exception {
        User receiver = createTestUser("kc-receiver", "receiver", "receiver@gmail.com");

        mockMvc.perform(post("/api/platform/users/{receiverId}/send-friend-request", receiver.getId().id()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendFriendRequest_ShouldFail_WhenSendingToSelf() throws Exception {
        // Arrange
        User user = createTestUser("kc-user", "user", "user@gmail.com");

        // Act & Assert
        mockMvc.perform(post("/api/platform/users/{receiverId}/send-friend-request", user.getId().id())
                        .with(validJwt(user.getKeycloakId(), "user")))
                .andExpect(status().isBadRequest());
    }
}