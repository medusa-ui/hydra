package io.getmedusa.hydra.discovery.service;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final DynamicRouteProvider dynamicRouteProvider;
    public RouteService(DynamicRouteProvider dynamicRouteProvider) {
        this.dynamicRouteProvider = dynamicRouteProvider;
    }

    @Bean
    @Primary
    //@ConditionalOnMissingBean(name = "cachedCompositeRouteLocator")
    public RouteLocator cachedCompositeRouteLocator(List<RouteLocator> routeLocators) {
        return dynamicRouteProvider;
    }

    public void add(ActiveService activeService) {
        dynamicRouteProvider.add(activeService);
        dynamicRouteProvider.reload();
    }

    public void remove(ActiveService activeService) {
        dynamicRouteProvider.remove(activeService);
        dynamicRouteProvider.reload();
    }
}
