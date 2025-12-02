package be.kdg.ipj3.users.application;

import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.shared.domain.UserId;
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
class GetAllPublicUsersTests {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnAllPublicUsers_fromRepository() {
        // Arrange
        User user1 = new User(new UserId(UUID.randomUUID()), "kc1", "user1", new Profile("User 1", "Bio 1"));
        User user2 = new User(new UserId(UUID.randomUUID()), "kc2", "user2", new Profile("User 2", "Bio 2"));
        given(userRepository.findAllPublicUsers()).willReturn(List.of(user1, user2));

        // Act
        List<User> result = userService.GetAllPublicUsers();

        // Assert
        assertThat(result).containsExactly(user1, user2);
        verify(userRepository).findAllPublicUsers();
    }

    @Test
    void shouldReturnEmptyList_whenNoPublicUsers() {
        // Arrange
        given(userRepository.findAllPublicUsers()).willReturn(List.of());

        // Act
        List<User> result = userService.GetAllPublicUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository).findAllPublicUsers();
    }
}
