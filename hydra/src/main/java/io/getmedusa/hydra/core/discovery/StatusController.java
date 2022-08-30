package io.getmedusa.hydra.core.discovery;

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import io.getmedusa.hydra.core.repository.meta.InMemoryStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StatusController {

    private final InMemoryStorage inMemoryStorage;
    public StatusController(InMemoryStorage inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @GetMapping("/health")
    public Map<String, String> showHealth() {
        return Map.of("status", "ok");
    }

    @GetMapping("/_h/services")
    public Map<String, List<ActiveService>> showActiveServices() {
        return inMemoryStorage.retrieveServiceMap();
    }

}
