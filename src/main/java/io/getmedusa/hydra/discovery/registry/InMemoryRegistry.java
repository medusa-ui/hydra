package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryRegistry {

    private final Map<String, ActiveService> sessionMap = new HashMap<>();

    public Map<String, ActiveService> getSessionMap() {
        return sessionMap;
    }

    public void add(String sessionId, ActiveService activeService) {
        System.out.println("Incoming registration from: " + activeService.getHost() + ":" + activeService.getPort() + " w/ endpoints: " + activeService.getEndpoints().size());
        this.sessionMap.put(sessionId, activeService);
    }

    public ActiveService getAndRemove(String sessionId) {
        final ActiveService activeService = this.sessionMap.get(sessionId);
        this.sessionMap.remove(sessionId);
        return activeService;
    }

    public Map<String, Set<String>> toURLMap() {
        Map<String, Set<String>> urlMap = new HashMap<>();
        for(Map.Entry<String, ActiveService> sessionEntry : sessionMap.entrySet()) {
            urlMap.put(sessionEntry.getValue().getName(), sessionEntry.getValue().getEndpoints());
        }
        return urlMap;
    }
}
