package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetFriendsForUserIntegrationTests extends BaseIntegrationTest {

    @Test
    void getFriends_shouldReturnFriends_forAuthenticatedUser_withAppropriateVisibility() throws Exception {
        // Arrange
        UUID friendId = UUID.randomUUID();
        UUID mainId = UUID.randomUUID();

        User mainUser = createTestUser("kc-main-" + mainId, "main-user", "main@gmail.com");
        User friendUser = createTestUser("kc-friend-" + friendId, "friend-user-" + friendId, "friend@gmail.com");

        mainUser.addFriend(friendUser.getId());
        friendUser.addFriend(mainUser.getId());
        userRepository.save(mainUser);
        userRepository.save(friendUser);

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/friends")
                        .with(validJwt(mainUser.getKeycloakId(), "main-user")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value(friendUser.getUsername()))
                .andExpect(jsonPath("$[0].id").value(friendUser.getId().id().toString()))
                .andExpect(jsonPath("$[0].profile.bio").value("bio"));
    }

    @Test
    void getFriends_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        createTestUser();

        // Act & Assert
        mockMvc.perform(get("/api/platform/users/friends"))
                .andExpect(status().isUnauthorized());
    }
}