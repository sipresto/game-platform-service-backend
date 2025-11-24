package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository {
    Optional<User> getUserByIdWithGames(UserId userId);
}
