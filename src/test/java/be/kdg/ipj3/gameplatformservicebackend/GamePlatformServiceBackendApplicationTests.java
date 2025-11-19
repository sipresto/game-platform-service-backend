package be.kdg.ipj3.gameplatformservicebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(TestcontainersConfiguration.class)
class GamePlatformServiceBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}

