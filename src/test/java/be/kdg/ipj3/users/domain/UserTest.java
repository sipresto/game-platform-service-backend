package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorStoresValuesCorrectly() {
        UserId id = new UserId(UUID.randomUUID().toString());
        List<GameId> games = List.of(GameId.create());

        User user = new User(id, "john", games);

        assertEquals(id, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals(games, user.getOwnedGames());
    }

    @Test
    void updateUsername_UpdatesSuccessfully() {
        User user = new User(new UserId("123"), "oldName", List.of());

        user.updateUsername("newName");

        assertEquals("newName", user.getUsername());
    }

    @Test
    void updateUsername_ThrowsWhenNull() {
        User user = new User(new UserId("123"), "name", List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> user.updateUsername(null)
        );
    }

    @Test
    void updateUsername_ThrowsWhenBlank() {
        User user = new User(new UserId("123"), "name", List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> user.updateUsername("  ")
        );
    }

    @Test
    void createFromKeycloak_CreatesValidUser() {
        String keycloakId = "kc123";
        String username = "alice";

        User user = User.createFromKeycloak(keycloakId, username);

        assertEquals(UserId.fromKeycloak(keycloakId), user.getId());
        assertEquals(username, user.getUsername());
        assertTrue(user.getOwnedGames().isEmpty());
    }
}
