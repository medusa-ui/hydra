package io.getmedusa.hydra.discovery.registry;

import java.util.Set;

public class KnownRoute {

    private String service;
    private Set<String> availableRoutes;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Set<String> getAvailableRoutes() {
        return availableRoutes;
    }

    public void setAvailableRoutes(Set<String> availableRoutes) {
        this.availableRoutes = availableRoutes;
    }
}
