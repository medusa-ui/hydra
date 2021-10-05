package io.getmedusa.hydra.status.model;

import java.util.HashSet;
import java.util.Set;

public class RouteStatus {

    private Set<String> activeRoutes = new HashSet<>();

    public Set<String> getActiveRoutes() {
        return activeRoutes;
    }

    public void setActiveRoutes(Set<String> activeRoutes) {
        this.activeRoutes = activeRoutes;
    }
}
