package be.kdg.ipj3.gameplatformservicebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"be.kdg.ipj3.shared.infrastructure"})
public class GamePlatformServiceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamePlatformServiceBackendApplication.class, args);
    }

}
