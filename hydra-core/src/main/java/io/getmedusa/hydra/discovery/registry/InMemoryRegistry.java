package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.MenuItem;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryRegistry {

    private final Map<String, ActiveService> sessionMap = new HashMap<>();
    private final Map<String, Set<MenuItem>> menuItems = new HashMap<>(); //menu name / items
    private final Map<String, Set<MenuItem>> menuSessions = new HashMap<>(); //session id / items

    public Map<String, ActiveService> getSessionMap() {
        return sessionMap;
    }

    public void add(String sessionId, ActiveService activeService) {
        System.out.println("Incoming registration from: " + activeService.getHost() + ":" + activeService.getPort() + " w/ endpoints: " + activeService.getEndpoints().size());
        this.sessionMap.put(sessionId, activeService);

        for(Map.Entry<String, Set<MenuItem>> menuItemEntrySet : activeService.getMenuItems().entrySet()) {
            final String key = menuItemEntrySet.getKey();
            final Set<MenuItem> items = new HashSet<>(menuItemEntrySet.getValue());
            if(this.menuItems.containsKey(key)) {
                this.menuItems.get(key).addAll(items);
            } else {
                this.menuItems.put(key, items);
            }

            for(MenuItem menuItem : menuItemEntrySet.getValue()) {
                Set<MenuItem> m = this.menuSessions.getOrDefault(sessionId, new HashSet<>());
                m.add(menuItem);
                this.menuSessions.put(sessionId, m);
            }
        }
    }

    public ActiveService getAndRemove(String sessionId) {
        final ActiveService activeService = this.sessionMap.get(sessionId);
        this.sessionMap.remove(sessionId);

        Set<MenuItem> menuItemsPotentiallyDeleted = this.menuSessions.get(sessionId);
        this.menuSessions.remove(sessionId);

        for(Set<MenuItem> setThatMightAlsoContainPotentialDeletions : this.menuSessions.values()) {
            menuItemsPotentiallyDeleted.removeIf(setThatMightAlsoContainPotentialDeletions::contains);
        }

        for(Set<MenuItem> items : menuItems.values()) {
            menuItemsPotentiallyDeleted.forEach(items::remove);
        }

        return activeService;
    }

    public KnownRoutes toURLMap() {
        KnownRoutes knownRoutes = new KnownRoutes();
        List<KnownRoute> routeList = new ArrayList<>();
        Map<String, ActiveService> clonedSessionMap = new HashMap<>(sessionMap);
        for(Map.Entry<String, ActiveService> sessionEntry : clonedSessionMap.entrySet()) {
            KnownRoute route = new KnownRoute();
            route.setService(sessionEntry.getValue().getName());
            route.setAvailableRoutes(sessionEntry.getValue().getEndpoints());
            routeList.add(route);

        }
        knownRoutes.setKnownRoutes(routeList);
        return knownRoutes;
    }

    public Map<String, Set<MenuItem>> getMenuItems() {
        return menuItems;
    }


}
