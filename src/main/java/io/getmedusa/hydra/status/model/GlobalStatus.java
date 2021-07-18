package io.getmedusa.hydra.status.model;

import java.util.ArrayList;
import java.util.List;

public class GlobalStatus {

    private List<ServiceStatus> servicesStatus = new ArrayList<>();
    private RouteStatus routeStatus = new RouteStatus();

    public List<ServiceStatus> getServicesStatus() {
        return servicesStatus;
    }

    public void setServicesStatus(List<ServiceStatus> servicesStatus) {
        this.servicesStatus = servicesStatus;
    }

    public RouteStatus getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(RouteStatus routeStatus) {
        this.routeStatus = routeStatus;
    }
}
