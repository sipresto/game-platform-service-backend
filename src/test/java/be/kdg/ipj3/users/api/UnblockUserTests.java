package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UnblockUserTests extends BaseIntegrationTest {

    @Test
    void unblock_shouldSucceed_whenUserIsBlocked() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "user2@gmail.com");

        user1.block(user2.getId());
        userRepository.save(user1);

        mockMvc.perform(patch("/api/platform/users/{unblockId}/unblock", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isNoContent());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getBlockedUsers()).isEmpty();
        assertThat(updatedUser2.getBlockedUsers()).isEmpty();
    }

    @Test
    void unblock_shouldFail_whenUserIsNotBlocked() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");
        User user2 = createTestUser("kc-user2", "User2", "user2@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{unblockId}/unblock", user2.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isBadRequest());

        User updatedUser1 = userRepository.findById(user1.getId()).orElseThrow();
        User updatedUser2 = userRepository.findById(user2.getId()).orElseThrow();

        assertThat(updatedUser1.getBlockedUsers()).isEmpty();
        assertThat(updatedUser2.getBlockedUsers()).isEmpty();
    }

    @Test
    void unblock_shouldFail_whenIdsAreTheSame() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{unblockId}/unblock", user1.getId().id())
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unblock_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        User user1 = createTestUser("kc-user1", "User1", "user1@gmail.com");

        mockMvc.perform(patch("/api/platform/users/{unblockId}/unblock", user1.getId().id()))
                .andExpect(status().isUnauthorized());
    }
}