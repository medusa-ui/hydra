package io.getmedusa.hydra.discovery.service;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class DynamicRouteProvider extends CachingRouteLocator {

    private final Set<ActiveService> activeServices = new HashSet<>();
    private final RouteLocatorBuilder builder;

    public DynamicRouteProvider(RouteLocatorBuilder builder) {
        super(new NoopLocator());
        this.builder = builder;
    }

    private Flux<Route> routeFlux = Flux.empty();

    public void reload() {
        final Map<String, String> routesMap = new HashMap<>();
        for(ActiveService activeService : activeServices) {
            String baseURI = activeService.toBaseURI();
            for(String endpoint : activeService.getEndpoints()) routesMap.put(endpoint, baseURI);
            for(String endpoint : activeService.getWebsockets()) routesMap.put("/event-emitter/" + endpoint, baseURI);
            for(String extension : activeService.getStaticResources()) routesMap.put("/**." + extension, baseURI);
        }

        final RouteLocatorBuilder.Builder routeBuilder = this.builder.routes();
        for(Map.Entry<String, String> entrySet : routesMap.entrySet()) {
            routeBuilder.route(r -> r.path(entrySet.getKey()).uri(entrySet.getValue()));
        }
        routeFlux = routeBuilder.build().getRoutes();
    }

    @Override
    public Flux<Route> getRoutes() {
        return routeFlux;
    }

    public void add(ActiveService activeService) {
        activeServices.add(activeService);
    }

    public void remove(ActiveService activeService) {
        activeServices.remove(activeService);
    }

    static class NoopLocator implements RouteLocator {
        @Override
        public Flux<Route> getRoutes() {
            return Flux.empty();
        }
    }
}
