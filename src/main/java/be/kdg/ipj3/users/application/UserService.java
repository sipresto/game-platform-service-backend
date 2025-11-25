package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserWithGames(UserId id) {
        return userRepository.findByIdWithGames(id)
                .orElseThrow(id::notFound);
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
}