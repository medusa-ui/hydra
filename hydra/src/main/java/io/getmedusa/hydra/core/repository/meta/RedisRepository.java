package io.getmedusa.hydra.core.repository.meta;

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
public interface RedisRepository {

    String retrieveOverallRouteHashKey();

    void storeActiveService(String key, ActiveService service);

    Map<String, Set<ActiveService>> retrieveServiceMap();

    ActiveService findService(String key);
    void updateAlive(String key);
}
