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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GetChatsByKeycloakIdTests {

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
    void getChatsByKeycloakId_shouldReturnEmptyList_whenNoChats() throws Exception {
        // Arrange
        createTestUser1();

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getChatsByKeycloakId_shouldReturnChatsOrderedByLastMessage_whenUserHasChats() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();
        UserId userId3 = createTestUser3();

        Chat chat1 = new Chat(null, Set.of(userId1, userId2));
        chat1.sendMessage(userId1, "Hello in chat 1");
        chatRepository.save(chat1);

        Chat chat2 = new Chat(null, Set.of(userId1, userId3));
        chat2.sendMessage(userId1, "Hello in chat 2");
        chat2.sendMessage(userId3, "Hi");
        chatRepository.save(chat2);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(chat2.getId().id().toString()))
                .andExpect(jsonPath("$[0].name").value("test-user-3"))
                .andExpect(jsonPath("$[0].lastMessage.content").value("Hi"))
                .andExpect(jsonPath("$[1].id").value(chat1.getId().id().toString()));
    }

    @Test
    void getChatsByKeycloakId_shouldOnlyShowUserChatsWithMessages_whenUserHasChats() throws Exception {
        // Arrange
        UserId userId1 = createTestUser1();
        UserId userId2 = createTestUser2();
        UserId userId3 = createTestUser3();

        // Chat without messages
        Chat chatWithoutMessages = new Chat(null, Set.of(userId1, userId2));
        chatRepository.save(chatWithoutMessages);

        // Chat with messages
        Chat chatWithMessages = new Chat(null, Set.of(userId1, userId3));
        chatWithMessages.sendMessage(userId1, "Hello");
        chatRepository.save(chatWithMessages);

        Chat chatWithoutUser = new Chat(null, Set.of(userId2, userId3));
        chatWithoutUser.sendMessage(userId2, "Hi");
        chatRepository.save(chatWithoutUser);

        // Act & Assert
        mockMvc.perform(get("/api/platform/chats")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(chatWithMessages.getId().id().toString()));
    }

    @Test
    void getChatsByKeycloakId_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/chats"))
                .andExpect(status().isUnauthorized());
    }
}