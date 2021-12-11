package io.getmedusa.hydra.status.controller;

import io.getmedusa.hydra.discovery.controller.ServiceController;
import io.getmedusa.hydra.status.model.GlobalStatus;
import io.getmedusa.hydra.status.model.RouteStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class StatusController {

    private final ServiceController serviceController;

    public StatusController(ServiceController serviceController) {
        this.serviceController = serviceController;
    }

    @GetMapping("/_hydra/status")
    public Mono<GlobalStatus> showStatus() {
        serviceController.sendURLMapToAll();
        return Mono.just(determineGlobalStatus());
    }

    private GlobalStatus determineGlobalStatus() {
        GlobalStatus globalStatus = new GlobalStatus();
        final RouteStatus routeStatus = new RouteStatus();

        globalStatus.setRouteStatus(routeStatus);

        return globalStatus;
    }

}
