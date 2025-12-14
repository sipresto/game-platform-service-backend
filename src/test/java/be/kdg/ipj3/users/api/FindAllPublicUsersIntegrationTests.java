package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class FindAllPublicUsersIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private void createTestUsers() {
        JpaUserEntity publicUser = new JpaUserEntity(
                UUID.randomUUID(),
                "kc-public",
                "public-user",
                new JpaProfile("Public", "I am public"),
                0,
                Visibility.PUBLIC,
                "Test@gmail.com"
        );

        JpaUserEntity privateUser = new JpaUserEntity(
                UUID.randomUUID(),
                "kc-private",
                "private-user",
                new JpaProfile("Private", "I am private"),
                0,
                Visibility.PRIVATE,
                "Test@gmail.com"
        );

        userRepository.save(publicUser);
        userRepository.save(privateUser);
    }

    @Test
    void getAllPublicUsers_shouldReturnOnlyPublicUsers_whenBothExist() throws Exception {
        // Arrange
        createTestUsers();

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("public-user"))
                .andExpect(jsonPath("$[0].profile.displayName").value("Public"));
    }

    @Test
    void getAllPublicUsers_shouldReturnEmptyList_whenNoUsersExist() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
