package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserJpaEntityTest {

    @Test
    void testFromDomain_MapsCorrectly() {
        UserId id = new UserId(UUID.randomUUID().toString());
        GameId gameId = GameId.create();

        User domain = new User(id, "alice", List.of(gameId));

        UserJpaEntity entity = UserJpaEntity.fromDomain(domain);

        assertEquals(id.value(), entity.getUserId());
        assertEquals("alice", entity.getUsername());
        assertEquals(1, entity.getOwnedGames().size());
        assertEquals(gameId.id(), entity.getOwnedGames().get(0));
    }

    @Test
    void testToDomain_MapsCorrectly() {
        UUID gid = UUID.randomUUID();
        UserJpaEntity entity = new UserJpaEntity(
                "123",
                "bob",
                List.of(new GameId(gid))
        );

        User domain = entity.toDomain();

        assertEquals("123", domain.getId().value());
        assertEquals("bob", domain.getUsername());
        assertEquals(1, domain.getOwnedGames().size());
        assertEquals(gid, domain.getOwnedGames().get(0).id());
    }
}