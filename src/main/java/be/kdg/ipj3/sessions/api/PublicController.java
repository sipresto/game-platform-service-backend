package be.kdg.ipj3.sessions.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping("/public/health")
    public Map<String, String> publicEndpoint() {
        return Map.of("status", "ok", "access", "public");
    }
}
