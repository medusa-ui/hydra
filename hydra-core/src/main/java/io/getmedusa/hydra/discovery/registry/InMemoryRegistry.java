package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.MenuItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryRegistry {

    private final Map<String, ActiveService> sessionMap = new HashMap<>();
    private final Map<String, List<MenuItem>> menuItems = new HashMap<>();

    public Map<String, ActiveService> getSessionMap() {
        return sessionMap;
    }

    public void add(String sessionId, ActiveService activeService) {
        System.out.println("Incoming registration from: " + activeService.getHost() + ":" + activeService.getPort() + " w/ endpoints: " + activeService.getEndpoints().size());
        this.sessionMap.put(sessionId, activeService);

        for(Map.Entry<String, List<MenuItem>> menuItem : activeService.getMenuItems().entrySet()) {
            final String key = menuItem.getKey();
            final List<MenuItem> items = menuItem.getValue();
            if(this.menuItems.containsKey(key)) {
                this.menuItems.get(key).addAll(items);
            } else {
                this.menuItems.put(key, items);
            }
        }
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

    public Map<String, List<MenuItem>> getMenuItems() {
        return menuItems;
    }


}
