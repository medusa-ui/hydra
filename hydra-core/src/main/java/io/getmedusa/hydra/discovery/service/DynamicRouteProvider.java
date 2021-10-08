package io.getmedusa.hydra.discovery.service;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Overriding Cloud Gateway's default location logic with our own maintained set of custom {@link ActiveService} objects
 * And a method 'reload()' to allow for service discovery. In other words, we can react to service come on- and offline.
 */
@Component
public class DynamicRouteProvider extends CachingRouteLocator {

    private final Set<ActiveService> activeServices = new HashSet<>();
    private final PathRoutePredicateFactory predicateFactory;

    //important here that the Flux is not final, we specifically want to be able to reload this
    private Flux<Route> routeFlux = Flux.empty();

    public DynamicRouteProvider(PathRoutePredicateFactory predicateFactory) {
        super(new NoopLocator());
        this.predicateFactory = predicateFactory;
    }

    public void reload() {
        final Set<Route> routes = new HashSet<>();
        for(ActiveService activeService : activeServices) {
            String baseURI = activeService.toBaseURI();
            for(String endpoint : activeService.getEndpoints()) routes.add(buildRoute(endpoint, baseURI));
            for(String endpoint : activeService.getWebsockets()) routes.add(buildRoute("/event-emitter/" + endpoint, baseURI));
            for(String extension : activeService.getStaticResources()) routes.add(buildRoute("/**." + extension, baseURI));
        }
        this.routeFlux = Mono.just(routes).flatMapMany(Flux::fromIterable);
    }

    private Route buildRoute(String path, String uri) {
        return Route.async()
                .id(path + "$" + uri)
                .uri(uri)
                .predicate(predicateFactory.apply(c -> c.setPatterns(List.of(path))))
                .replaceFilters(new ArrayList<>())
                .build();
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
