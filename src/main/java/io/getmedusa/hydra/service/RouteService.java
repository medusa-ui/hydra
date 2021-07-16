package io.getmedusa.hydra.service;

import io.getmedusa.hydra.model.ActiveService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean(name = "cachedCompositeRouteLocator")
    public RouteLocator cachedCompositeRouteLocator(List<RouteLocator> routeLocators) {
        return dynamicRouteProvider;
    }

    public void add(ActiveService activeService) {
        System.out.println(activeService);
        dynamicRouteProvider.reload();
    }
}
