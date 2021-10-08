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

/**
 * Overriding Cloud Gateway's default location logic with our own maintained set of custom {@link ActiveService} objects
 * And a method 'reload()' to allow for service discovery. In other words, we can react to service come on- and offline.
 */
@Component
public class DynamicRouteProvider extends CachingRouteLocator {

    private final Set<ActiveService> activeServices = new HashSet<>();
    private final RouteLocatorBuilder builder;

    //important here that the Flux is not final, we specifically want to be able to reload this
    private Flux<Route> routeFlux = Flux.empty();

    public DynamicRouteProvider(RouteLocatorBuilder builder) {
        super(new NoopLocator());
        this.builder = builder;
    }

    public void reload() {
        final Map<String, ActiveService> routesMap = new HashMap<>();
        for(ActiveService activeService : activeServices) {
            for(String endpoint : activeService.getEndpoints()) routesMap.put(endpoint, activeService);
            for(String endpoint : activeService.getWebsockets()) routesMap.put("/event-emitter/" + endpoint, activeService);
            for(String extension : activeService.getStaticResources()) routesMap.put("/**." + extension, activeService);
        }

        final RouteLocatorBuilder.Builder routeBuilder = this.builder.routes();
        for(Map.Entry<String, ActiveService> pathToUriMap : routesMap.entrySet()) {
            final ActiveService activeService = pathToUriMap.getValue();
            routeBuilder.route(r -> r
                    .path(pathToUriMap.getKey())
                    .filters(f -> f.addRequestHeader("hydra-path", Integer.toString(activeService.hashCode())))
                    .uri(activeService.toBaseURI()));
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

    /**
     * Negating the use of RouteLocator, all our routes comes from the dynamic {@link ActiveService} set.
     */
    static class NoopLocator implements RouteLocator {
        @Override
        public Flux<Route> getRoutes() {
            return Flux.empty();
        }
    }
}
