package be.kdg.ipj3.social.api.chatController;

import be.kdg.ipj3.social.domain.Chat;
import be.kdg.ipj3.social.domain.ChatRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetChatByIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        chatRepository.deleteAll();
        userRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private UserId createTestUser1() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "test@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private UserId createTestUser2() {
        User user = User.createFromKeycloak("keycloak-id-2", "test-user-2", "test2@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    private UserId createTestUser3() {
        User user = User.createFromKeycloak("keycloak-id-3", "test-user-3", "test3@gmail.com");
        userRepository.save(user);
        return user.getId();
    }

    @Test
    void getChatById_shouldReturnChatDto_whenUserIsParticipant() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(null, Set.of(userId1, userId2));
        chat.sendMessage(userId1, "Hello!");
        chat.sendMessage(userId2, "Hi there!");
        chatRepository.save(chat);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats/" + chat.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(chat.getId().id().toString()))
                .andExpect(jsonPath("$.participants", hasSize(2)))
                .andExpect(jsonPath("$.participants[*].id",
                        containsInAnyOrder(
                                userId1.id().toString(),
                                userId2.id().toString()
                        )))
                .andExpect(jsonPath("$.participants[*].username",
                        containsInAnyOrder("test-user", "test-user-2")))
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages[0].content").value("Hello!"))
                .andExpect(jsonPath("$.messages[0].isMine").value(true))
                .andExpect(jsonPath("$.messages[1].content").value("Hi there!"))
                .andExpect(jsonPath("$.messages[1].isMine").value(false));
    }

    @Test
    void getChatById_shouldReturnEmptyMessages_whenNoMessages() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(null, Set.of(userId1, userId2));
        chatRepository.save(chat);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats/" + chat.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(0)));
    }

    @Test
    void getChatById_shouldReturnForbidden_whenUserNotParticipant() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();
        UserId userId3 = createTestUser3();

        Chat chat = new Chat(null, Set.of(userId2, userId3));
        chatRepository.save(chat);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats/" + chat.getId().id())
                        .with(validJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getChatById_shouldReturnNotFound_whenChatDoesNotExist() throws Exception {
        // Arrange
        createTestUser1();

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats/" + UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatById_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();

        Chat chat = new Chat(null, Set.of(userId1, userId2));
        chatRepository.save(chat);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats/" + chat.getId().id()))
                .andExpect(status().isUnauthorized());
    }
}