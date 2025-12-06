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

    public User findById(UserId id) {
        return userRepository.findById(id)
                .orElseThrow(id::notFound);
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

    public void addGame(UserId id, GameId gameId) {
        User user = userRepository.findById(id).orElseThrow(id::notFound);
        user.addGame(gameId);
        userRepository.save(user);
    }

    public List<GameId> findOwnedGamesByKeycloakId(String keycloakId) {
        User user = findByKeycloakId(keycloakId);
        return userRepository.findOwnedGamesById(keycloakId);
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

    public boolean existsById(UserId id) {
        return userRepository.existsById(id);
    }
}