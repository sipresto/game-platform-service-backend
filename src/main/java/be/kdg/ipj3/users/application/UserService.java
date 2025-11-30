package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register or get existing user from Keycloak JWT
     * This is called automatically on first login
     */
    public User registerOrGetUser(String keycloakId, String keycloakUsername) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak id cannot be null or blank");
        }

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = User.createFromKeycloak(keycloakId, keycloakUsername);
                    userRepository.save(newUser);
                    return newUser;
                });
    }

    public User getUser(UserId id) {
        return userRepository.findById(id)
                .orElseThrow(id::notFound);
    }

    public void updateUsername(String keycloakId, String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("New username cannot be null or blank");
        }

        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak id cannot be null or blank");
        }

        User user = userRepository.findByKeycloakId(keycloakId)
                        .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
        user.updateUsername(newUsername);
        userRepository.save(user);
    }

    public void addGame(UserId id, GameId gameId) {
        User user = userRepository.findById(id).orElseThrow(id::notFound);
        user.addGame(gameId);
        userRepository.save(user);
    }

    public List<GameId> findOwnedGamesByKeycloakId(String keycloakId) {
        return userRepository.findOwnedGamesByKeycloakId(keycloakId);
    }

    public Optional<User> findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }
}