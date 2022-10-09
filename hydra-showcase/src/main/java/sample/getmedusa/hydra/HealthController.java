package sample.getmedusa.hydra;

import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class HealthController {

    @GetMapping("/health2")
    public Map<String, String> showHealth(final ServerWebExchange exchange) {
        exchange.getResponse().addCookie(
                ResponseCookie.from("HYDRA-SSO", UUID.randomUUID().toString())
                        .httpOnly(true)
                        .maxAge(Duration.ofHours(12))
                        .build());

        Map<String, String> health = new HashMap<>();
        health.put("test", "123");
        return health;
    }

}
