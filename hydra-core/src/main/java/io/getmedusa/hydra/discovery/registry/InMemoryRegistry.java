package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.MenuItem;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryRegistry {

    private final Map<String, ActiveService> sessionMap = new HashMap<>();
    private final Map<String, List<MenuItem>> menuItems = new HashMap<>();
    private final Map<String, Set<String>> menuSessions = new HashMap<>();

    public Map<String, ActiveService> getSessionMap() {
        return sessionMap;
    }

    public void add(String sessionId, ActiveService activeService) {
        System.out.println("Incoming registration from: " + activeService.getHost() + ":" + activeService.getPort() + " w/ endpoints: " + activeService.getEndpoints().size());
        this.sessionMap.put(sessionId, activeService);

        for(Map.Entry<String, List<MenuItem>> menuItem : activeService.getMenuItems().entrySet()) {
            final String key = menuItem.getKey();
            final List<MenuItem> items = new ArrayList<>(menuItem.getValue());
            if(this.menuItems.containsKey(key)) {
                this.menuItems.get(key).addAll(items);
            } else {
                this.menuItems.put(key, items);
            }
        }
        this.menuSessions.put(sessionId, activeService.getMenuItems().keySet());
    }

    public ActiveService getAndRemove(String sessionId) {
        final ActiveService activeService = this.sessionMap.get(sessionId);
        this.sessionMap.remove(sessionId);

        Set<String> keysPotentiallyDeleted = new HashSet<>(this.menuSessions.get(sessionId));
        this.menuSessions.remove(sessionId);
        for(String keyToPotentiallyDeleted : keysPotentiallyDeleted) {
            if(!isPresentWithOtherSession(keyToPotentiallyDeleted)) {
                this.menuItems.remove(keyToPotentiallyDeleted);
            }
        }

        return activeService;
    }

    private boolean isPresentWithOtherSession(String key) {
        for(Set<String> sessionKeys : this.menuSessions.values()) {
            if(sessionKeys.contains(key)){
                return true;
            }
        }
        return false;
    }

    public KnownRoutes toURLMap() {
        KnownRoutes knownRoutes = new KnownRoutes();
        List<KnownRoute> routeList = new ArrayList<>();
        for(Map.Entry<String, ActiveService> sessionEntry : sessionMap.entrySet()) { //TODO possible issue with concurrent map
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
