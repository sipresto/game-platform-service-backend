package be.kdg.ipj3.users.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetAllPublicUsersIntegrationTests extends BaseIntegrationTest {

    private void createTestUsers() {
        Map<VisibleField, Visibility> publicVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        Map<VisibleField, Visibility> privateVisibility = VisibilitySettings.defaultPublic().fieldVisibilities();
        privateVisibility.put(VisibleField.PROFILE, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.POINTS, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.FAVORITE_GAMES, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.OWNED_GAMES, Visibility.PRIVATE);
        privateVisibility.put(VisibleField.FRIENDS_LIST, Visibility.PRIVATE);

        var publicVisibilitySettings = new VisibilitySettings(publicVisibility);
        var privateVisibilitySettings = new VisibilitySettings(privateVisibility);

        User publicUser = new User(
                new UserId(UUID.randomUUID()),
                "kc-public",
                "public-user",
                List.of(),
                List.of(),
                null,
                100,
                new Profile("Public", "I am public", null, null),
                publicVisibilitySettings,
                "public@gmail.com"
        );

        User privateUser = new User(
                new UserId(UUID.randomUUID()),
                "kc-private",
                "private-user",
                List.of(),
                List.of(),
                null,
                50,
                new Profile("Private", "I am private", null, null),
                privateVisibilitySettings,
                "private@gmail.com"
        );

        userRepository.save(publicUser);
        userRepository.save(privateUser);
    }

    @Test
    void getAllUsers_shouldReturnAllUsersWithRespectiveVisibility_whenBothExist() throws Exception {
        // Arrange
        createTestUsers();

        // Act & Assert
        mockMvc.perform(get("/api/platform/users")
                        .with(validJwt("kc-public", "public-user")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.username=='public-user')].profile.bio").value("I am public"))
                .andExpect(jsonPath("$[?(@.username=='public-user')].points").value(100))
                .andExpect(jsonPath("$[?(@.username=='private-user')].profile.bio").doesNotExist())
                .andExpect(jsonPath("$[?(@.username=='private-user')].points").doesNotExist());
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() throws Exception {
        mockMvc.perform(get("/api/platform/users")
                        .with(validJwt("kc-public", "public-user")))
                .andExpect(status().isNotFound());
    }
}