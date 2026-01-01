package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetBlockedUsersTests extends BaseIntegrationTest {

    @Test
    void getBlockedUsers_shouldReturnFilledList_whenBlockedUsersExist() throws Exception {
        User user1 = createTestUser("user1", "user1", "user1@gmail.com");
        User user2 = createTestUser("user2", "user2", "user2@gmail.com");

        user1.block(user2.getId());
        userRepository.save(user1);

        mockMvc.perform(get("/api/platform/users/blocked")
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user2.getId().id().toString()))
                .andExpect(jsonPath("$[0].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[0].profile.bio").value(user2.getProfile().bio()));
    }

    @Test
    void getBlockedUsers_shouldReturnEmptyList_whenNoBlockedUsersExist() throws Exception {
        User user1 = createTestUser("user1", "user1", "user1@gmail.com");

        mockMvc.perform(get("/api/platform/users/blocked")
                        .with(validJwt(user1.getKeycloakId(), user1.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getBlockedUsers_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/platform/users/blocked"))
                .andExpect(status().isUnauthorized());
    }
}