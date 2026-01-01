package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RemoveFriendTests extends BaseIntegrationTest {

    @Test
    void removeFriends_shouldSucceed_whenUsersAreFriends() throws Exception {
        // Arrange
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "user2@gmail.com");

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());

        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        mockMvc.perform(patch("/api/platform/users/{friendId}/remove", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), "User1")))
                .andExpect(status().isNoContent());

        // Assert
        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getFriends()).isEmpty();
        assertThat(updatedUser2.getFriends()).isEmpty();
    }

    @Test
    void removeFriends_shouldFail_whenUsersAreNotFriends() throws Exception {
        // Arrange
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "user2@gmail.com");

        // Act & Assert
        mockMvc.perform(patch("/api/platform/users/{friendId}/remove", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), "User1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFriends_shouldFail_whenUserIdsAreTheSame() throws Exception {
        // Arrange
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");

        // Act & Assert
        mockMvc.perform(patch("/api/platform/users/{friendId}/remove", user1.getId().id())
                        .with(validJwt(user1.getKeycloakId(), "User1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFriends_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");

        // Act & Assert
        mockMvc.perform(patch("/api/platform/users/{friendId}/remove", user1.getId().id()))
                .andExpect(status().isUnauthorized());
    }
}