package be.kdg.ipj3.users.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UsersApi {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register or get existing user fromDomain Keycloak JWT
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

    public User findById(UUID id) {
        var userId = new UserId(id);
        return userRepository.findById(userId)
                .orElseThrow(userId::notFound);
    }

    public void updateProfile(String keycloakId, String displayName, String newBio) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak id cannot be null or blank");
        }

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));

        user.updateProfile(new Profile(displayName, newBio));
        userRepository.save(user);
    }

    public void addGame(UUID id, UUID gameId) {
        var userId = new UserId(id);

        User user = userRepository.findById(userId).orElseThrow(userId::notFound);
        user.addGame(new GameId(gameId));
        userRepository.save(user);
    }

    public List<GameId> findOwnedGamesById(UUID id) {
        return userRepository.findOwnedGamesById(new UserId(id));
    }

    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User with keycloak id [" + keycloakId + "] not found"));
    }

    public void updatePrivacySettingsByKeycloakId(Visibility visibility, User user) {
        user.updateVisibility(visibility);
        userRepository.save(user);
    }

    public List<User> findAllPublicUsers() {
        return userRepository.findAllPublicUsers();
    }

    public List<User> findFriendsAndSelf(User user) {
        List<User> usersReturn = new ArrayList<>();
        usersReturn.add(user);
        usersReturn.addAll(userRepository.findFriends(user.getId()));
        return usersReturn;
    }

    public boolean existsById(UUID id) {
        return userRepository.existsById(new UserId(id));
    }

    public UserId findIdByKeycloakId(String keycloakId) {
        return findByKeycloakId(keycloakId).getId();
    }
}