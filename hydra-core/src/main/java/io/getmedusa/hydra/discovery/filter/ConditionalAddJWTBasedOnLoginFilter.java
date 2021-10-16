package io.getmedusa.hydra.discovery.filter;

import io.getmedusa.hydra.security.JWTTokenService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class ConditionalAddJWTBasedOnLoginFilter extends AbstractGatewayFilterFactory<ConditionalAddJWTBasedOnLoginFilter.Config> {

    private static final String BEARER = "Bearer ";
    private static final String AUTH = "Authorization";

    private final JWTTokenService jwtTokenService;

    public ConditionalAddJWTBasedOnLoginFilter(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public String name() {
        return "ConditionalAddJWTBasedOnLoginFilter";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(httpHeaders -> httpHeaders.add(AUTH, BEARER + jwtTokenService.generateToken()))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {

    }

}
