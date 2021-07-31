package io.getmedusa.hydra.status.controller;

import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.status.model.GlobalStatus;
import io.getmedusa.hydra.status.model.RouteStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class StatusController {

    private final InMemoryRegistry inMemoryRegistry;

    public StatusController(InMemoryRegistry inMemoryRegistry) {
        this.inMemoryRegistry = inMemoryRegistry;
    }

    @GetMapping("/hydra/status")
    public Mono<GlobalStatus> showStatus() {
        return Mono.just(determineGlobalStatus());
    }

    private GlobalStatus determineGlobalStatus() {
        GlobalStatus globalStatus = new GlobalStatus();
        final RouteStatus routeStatus = new RouteStatus();

        //final Map<String, ActiveService> serviceMap = inMemoryRegistry.getServiceMap();
        //routeStatus.setActiveRoutes(serviceMap.keySet());
        globalStatus.setRouteStatus(routeStatus);

        /*List<ServiceStatus> servicesStatus = serviceMap.values().stream().map(s -> {
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setName(s.getName());
            serviceStatus.setUpSince(s.getActiveSince());
            return serviceStatus;
        }).collect(Collectors.toList());*/
        //globalStatus.setServicesStatus(servicesStatus);
        return globalStatus;
    }

}
