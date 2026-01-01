package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlockUserTests extends BaseIntegrationTest {

    @Test
    void block_shouldSucceed_whenUsersAreNotFriends() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "User1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "User2@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{blockId}/block", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isNoContent());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getBlockedUsers()).hasSize(1);
        assertThat(updatedUser2.getBlockedUsers()).hasSize(0);
    }

    @Test
    void block_shouldSucceed_whenUsersAreFriends() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "User1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "User2@gmail.com");

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());

        userRepository.save(user1);
        userRepository.save(user2);

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isNoContent());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getFriends()).hasSize(0);
        assertThat(updatedUser2.getFriends()).hasSize(0);

        assertThat(updatedUser1.getBlockedUsers()).hasSize(1);
    }

    @Test
    void block_shouldFail_whenIdsAreTheSame() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "User1@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user1.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void block_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "User1@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{friendId}/block", user1.getId().id()))
                .andExpect(status().isUnauthorized());
    }
}