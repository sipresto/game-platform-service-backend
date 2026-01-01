package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.FriendRequest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class AcceptFriendRequestIntegrationTests extends BaseIntegrationTest {

    @Test
    void acceptFriendRequest_ShouldSucceed_WhenRequestExists() throws Exception {
        // Arrange
        User sender = createTestUser("kc-otherUser", "otherUser","other@gmail.com");
        User receiver = createTestUser("kc-receiver", "receiver","other@gmail.com");

        receiver.addIncomingFriendRequest(new FriendRequest(sender.getId()));
        userRepository.save(receiver);

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", sender.getId().id())
                        .with(validJwt(receiver.getKeycloakId(),"receiver")))
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
        User sender = createTestUser("kc-otherUser", "otherUser","other@gmail.com");
        User receiver = createTestUser("kc-receiver", "receiver","other@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", sender.getId().id())
                        .with(validJwt(receiver.getKeycloakId(),"receiver")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptFriendRequest_ShouldFail_WhenSenderDoesNotExist() throws Exception {
        // Arrange
        User receiver = createTestUser("kc-receiver", "receiver","other@gmail.com");

        // Act
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", UUID.randomUUID())
                        .with(validJwt(receiver.getKeycloakId(),"receiver")))
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
        User user = createTestUser("kc-otherUser", "otherUser","other@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/users/{senderId}/friend-request/accept", user.getId().id())
                        .with(validJwt(user.getKeycloakId(),"User")))
                .andExpect(status().isBadRequest());
    }
}