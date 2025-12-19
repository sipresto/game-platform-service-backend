package be.kdg.ipj3.users.application;

import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetFriendsOfUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserPlusFriends_whenFriendsExist() {
        // Arrange
        User mainUser = new User(new UserId(UUID.randomUUID()), "kc-main", "main", new Profile("Main", "Bio"), 0, "Test@gmail.com");
        User friend1 = new User(new UserId(UUID.randomUUID()), "kc-f1", "friend1", new Profile("Friend1", "Bio"), 0, "Test@gmail.com");
        User friend2 = new User(new UserId(UUID.randomUUID()), "kc-f2", "friend2", new Profile("Friend2", "Bio"), 0, "Test@gmail.com");

        given(userRepository.findFriends(mainUser.getId())).willReturn(List.of(friend1, friend2));

        // Act
        List<User> result = userService.findFriendsAndSelf(mainUser);

        // Assert
        assertThat(result).containsExactly(mainUser, friend1, friend2);
        verify(userRepository).findFriends(mainUser.getId());
    }

    @Test
    void shouldReturnOnlyUser_whenNoFriends() {
        // Arrange
        User mainUser = new User(new UserId(UUID.randomUUID()), "kc-main", "main", new Profile("Main", "Bio"), 0, "Test@gmail.com");
        given(userRepository.findFriends(mainUser.getId())).willReturn(List.of());

        // Act
        List<User> result = userService.findFriendsAndSelf(mainUser);

        // Assert
        assertThat(result).containsExactly(mainUser);
        verify(userRepository).findFriends(mainUser.getId());
    }
}
