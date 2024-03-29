package io.getmedusa.hydra.core.repository;

//abstracts distinction between in-memory and redis storage

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import io.getmedusa.hydra.core.repository.meta.InMemoryStorage;
import io.getmedusa.hydra.core.repository.meta.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
public class MemoryRepository {

    private final InMemoryStorage inMemoryStorage;
    private final RedisRepository redis;
    private final boolean hasRedis;

    public MemoryRepository(InMemoryStorage inMemoryStorage, @Autowired(required = false) RedisRepository redis) {
        this.inMemoryStorage = inMemoryStorage;
        this.hasRedis = redis != null;
        this.redis = redis;
    }

    public boolean hasRouteHashChanged() {
        if(hasRedis) {
            final String previousHash = inMemoryStorage.getOverallRouteHashKey();
            final String overallRouteHashKey = redis.retrieveOverallRouteHashKey();
            if(overallRouteHashKey == null) return previousHash != null;
            return !overallRouteHashKey.equals(previousHash);
        }
        return false;
    }

    public void storeActiveServices(String key, ActiveService service) {
        if(hasRedis) {
            redis.storeActiveService(key, service);
        } else {
            inMemoryStorage.storeActiveService(key, service);
        }
    }

    public Map<String, Set<ActiveService>> retrieveActiveService() {
        if(hasRedis) {
            return redis.retrieveServiceMap();
        } else {
            return inMemoryStorage.retrieveServiceMap();
        }
    }

    public ActiveService findService(String key) {
        if(hasRedis) {
            return redis.findService(key);
        } else {
            return inMemoryStorage.findService(key);
        }
    }

    public void updateAlive(String key) {
        if(hasRedis) {
            redis.updateAlive(key);
        } else {
            inMemoryStorage.updateAlive(key);
        }
    }
}
