package be.kdg.ipj3.users.application;

import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final IUserRepository userRepository;

    public UserService(
            @Qualifier("dbUserRepository") IUserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public User getUserWithGames(UserId userId) {
        return userRepository.getUserByIdWithGames(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }
}
