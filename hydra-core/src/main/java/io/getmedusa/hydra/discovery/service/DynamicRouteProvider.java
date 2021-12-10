package io.getmedusa.hydra.discovery.service;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

/**
 * Overriding Cloud Gateway's default location logic with our own maintained set of custom {@link ActiveService} objects
 * And a method 'reload()' to allow for service discovery. In other words, we can react to service come on- and offline.
 */
@Component
public class DynamicRouteProvider extends CachingRouteLocator {

    protected static final String SLASH = "/";

    private final Set<ActiveService> activeServices = new HashSet<>();
    private final RouteLocatorBuilder builder;
    private final WeightService weightService;

    //important here that the Flux is not final, we specifically want to be able to reload this
    private Flux<Route> routeFlux = Flux.empty();

    public DynamicRouteProvider(RouteLocatorBuilder builder, WeightService weightService) {
        super(new NoopLocator());
        this.builder = builder;
        this.weightService = weightService;
    }

    public void reload() {
        final RouteLocatorBuilder.Builder routeBuilder = this.builder.routes();

        for(ActiveService activeService : activeServices) {
            final String baseURI = activeService.toBaseURI();
            final String hydraPath = Integer.toString(activeService.hashCode());
            final String slashedHydraPath = SLASH + hydraPath + SLASH;

            for(String endpoint : activeService.getEndpoints()) {
                System.out.println(endpoint);
                routeBuilder.route(endpoint, r -> r.weight(endpoint, weightService.getWeight(endpoint)).and().path(endpoint)
                                        .filters(f -> f.addRequestHeader("hydra-path", hydraPath))
                                        .uri(baseURI));
            }

            for(String endpoint : activeService.getWebsockets()) {
                String hPath = SLASH + hydraPath + "/event-emitter/" + endpoint;
                routeBuilder.route(hPath, r -> r.weight(hPath, weightService.getWeight(hPath)).and().path(hPath)
                                        .filters(f -> f.rewritePath(slashedHydraPath, SLASH))
                                        .uri(baseURI));
            }

            for(String extension : activeService.getStaticResources()) {
                String ePath = SLASH + hydraPath + "/**." + extension;
                routeBuilder.route(ePath, r -> r.weight(ePath, weightService.getWeight(ePath)).and().path(ePath)
                                         .filters(f -> f
                                                     .addResponseHeader("Cache-Control", "private, max-age 30, max-stale 3600")
                                                     .rewritePath(slashedHydraPath, SLASH))
                                         .uri(baseURI));
            }
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
