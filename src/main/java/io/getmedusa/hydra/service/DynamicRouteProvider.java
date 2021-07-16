package io.getmedusa.hydra.service;

import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicRouteProvider extends CachingRouteLocator {

    private Map<String, String> routesMap = buildRouteMap();

    private final RouteLocatorBuilder builder;
    public DynamicRouteProvider(RouteLocatorBuilder builder) {
        super(new NoopLocator());
        this.builder = builder;
    }

    private Flux<Route> routeFlux = Flux.empty();

    public void reload() {
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

    static class NoopLocator implements RouteLocator {
        @Override
        public Flux<Route> getRoutes() {
            return Flux.empty();
        }
    }

    private static Map<String, String> buildRouteMap() {
        Map<String, String> map = new HashMap<>();
        map.put("/", "http://localhost:8080");
        map.put("/event-emitter/hello-world", "ws://localhost:8080/event-emitter/hello-world");
        map.put("/static/stylesheet.css", "http://localhost:8080/static/stylesheet.css");
        return map;
    }

}
