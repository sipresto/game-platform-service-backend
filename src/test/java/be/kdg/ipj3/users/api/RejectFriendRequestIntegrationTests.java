package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.FriendRequest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RejectFriendRequestIntegrationTests extends BaseIntegrationTest {

    @Test
    void rejectFriendRequest_ShouldSucceed_WhenRequestExists() throws Exception {
        // Arrange
        User sender = createTestUser("kc-otherUser", "otherUser", "other@gmail.com");
        User receiver = createTestUser("kc-receiver", "receiver", "receiver@gmail.com");

        receiver.addIncomingFriendRequest(new FriendRequest(sender.getId()));
        userRepository.save(receiver);

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/reject", sender.getId().id())
                        .with(validJwt(receiver.getKeycloakId(), "receiver")))
                .andExpect(status().isNoContent());

        // Assert
        User updatedReceiver = userRepository.findById(receiver.getId()).orElseThrow();

        assertThat(updatedReceiver.getFriends()).doesNotContain(sender.getId());
        assertThat(updatedReceiver.getIncomingFriendRequests()).isEmpty();
    }

    @Test
    void rejectFriendRequest_ShouldFail_WhenSenderDoesNotExist() throws Exception {
        // Arrange
        User receiver = createTestUser("kc-receiver", "receiver", "receiver@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/reject", UUID.randomUUID())
                        .with(validJwt(receiver.getKeycloakId(), "receiver")))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectFriendRequest_ShouldNotFail_WhenNoRequestExists() throws Exception {
        // Arrange
        User sender = createTestUser("kc-otherUser", "otherUser", "other@gmail.com");
        User receiver = createTestUser("kc-receiver", "receiver", "receiver@gmail.com");

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/reject", sender.getId().id())
                        .with(validJwt(receiver.getKeycloakId(), "receiver")))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectFriendRequest_ShouldFail_WhenUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/reject", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}