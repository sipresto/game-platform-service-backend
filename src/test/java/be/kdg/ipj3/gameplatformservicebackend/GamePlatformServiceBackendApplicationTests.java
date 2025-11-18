package be.kdg.ipj3.gameplatformservicebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class GamePlatformServiceBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
