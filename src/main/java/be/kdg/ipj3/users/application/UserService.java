package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import be.kdg.ipj3.users.infrastructure.jpa.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final IUserRepository userRepository;
    private final IUserRepository repo;

    public UserService(
            @Qualifier("dbUserRepository") IUserRepository userRepository
    ) {
        this.userRepository = userRepository;
    public UserService(IUserRepository repo) {
        this.repo = repo;
    }

    public User getUserWithGames(UserId userId) {
        return userRepository.getUserByIdWithGames(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    /**
     * Register or get existing user from Keycloak JWT
     * This is called automatically on first login
     */
    public User registerOrGetUser(String keycloakId, String keycloakUsername) {
        UserId userId = UserId.fromKeycloak(keycloakId);

        return repo.findById(userId)
                .orElseGet(() -> {
                    User newUser = User.createFromKeycloak(keycloakId, keycloakUsername);
                    repo.save(newUser);

                    Profile newProfile = Profile.createFromUser(newUser);
                    repo.save(newProfile);

                    return newUser;
                });
    }
}

    public Profile getProfile(UserId userId) {
        return repo.findProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public User getUser(UserId userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateUsername(UserId userId, String newUsername) {
        User user = getUser(userId);
        user.updateUsername(newUsername);
        repo.save(user);

        Profile profile = getProfile(userId);
        profile.updateUsername(newUsername);
        repo.save(profile);
    }
}