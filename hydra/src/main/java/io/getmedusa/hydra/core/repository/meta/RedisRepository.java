package io.getmedusa.hydra.core.repository.meta;

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
public class RedisRepository {
    public String retrieveOverallRouteHashKey() {
        return null;
    }

    public void storeActiveService(String key, ActiveService service) {
    }

    public Map<String, Set<ActiveService>> retrieveServiceMap() {
        return null;
    }

    public ActiveService findService(String key) {
        return null;
    }

    public void updateAlive(String key) {
    }
}
