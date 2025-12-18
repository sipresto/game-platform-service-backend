package be.kdg.ipj3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class GamePlatformServiceBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GamePlatformServiceBackendApplication.class, args);
    }
}
