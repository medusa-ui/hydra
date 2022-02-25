package io.getmedusa.hydra.status.controller;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.service.DynamicRouteProvider;
import io.getmedusa.hydra.discovery.service.WeightService;
import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;

import java.util.*;

@UIEventPage(path = "/_hydra/overview", file = "pages/overview.html")
public class OverviewController {

    private final DynamicRouteProvider routeProvider;
    private final WeightService weightService;

    public OverviewController(DynamicRouteProvider routeProvider, WeightService weightService) {
        this.routeProvider = routeProvider;
        this.weightService = weightService;
    }

    public PageAttributes setupAttributes() {
        List<OverviewEndpointModel> activeServices = toModel(routeProvider.getActiveServices());
        Collections.sort(activeServices);
        return new PageAttributes().with("activeServices", activeServices);
    }

    private List<OverviewEndpointModel> toModel(Set<ActiveService> activeServices) {
        List<OverviewEndpointModel> model = new ArrayList<>();
        for(ActiveService a : activeServices) {
            for(String endpoint : a.getEndpoints()) {
                if(!endpoint.equals("/login")) {
                    final String name = a.getName();
                    final String ip = a.getHost() + ":" + a.getPort();
                    final int weight = weightService.generateWeight(endpoint, a);
                    model.add(new OverviewEndpointModel(name, ip, "1.0.0", endpoint, weight));
                }
            }
        }
        return model;
    }

}
