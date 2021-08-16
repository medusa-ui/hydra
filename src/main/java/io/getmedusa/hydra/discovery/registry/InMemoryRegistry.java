package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public KnownRoutes toURLMap() {
        KnownRoutes knownRoutes = new KnownRoutes();
        List<KnownRoute> routeList = new ArrayList<>();
        for(Map.Entry<String, ActiveService> sessionEntry : sessionMap.entrySet()) {
            KnownRoute route = new KnownRoute();
            route.setService(sessionEntry.getValue().getName());
            route.setAvailableRoutes(sessionEntry.getValue().getEndpoints());
            routeList.add(route);

        }
        knownRoutes.setKnownRoutes(routeList);
        return knownRoutes;
    }
}
