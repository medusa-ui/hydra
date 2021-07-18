package io.getmedusa.hydra.discovery.controller;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.discovery.service.RouteService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class ServiceController {

    private final RouteService routeService;
    private final InMemoryRegistry inMemoryRegistry;

    public ServiceController(RouteService routeService, InMemoryRegistry inMemoryRegistry) {
        this.inMemoryRegistry = inMemoryRegistry;
        this.routeService = routeService;
    }

    @Bean
    RouterFunction<ServerResponse> setupRoutes() {
        return route()
                .GET("/services", accept(APPLICATION_JSON), this::getServices)
                .POST("/services/register", accept(APPLICATION_JSON), this::registerService)
                .POST("/services/kill", accept(APPLICATION_JSON), this::killService)
                .build();
    }

    public Mono<ServerResponse> getServices(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(inMemoryRegistry.getServiceMap());
    }

    public Mono<ServerResponse> registerService(ServerRequest request) {
        return request.body(BodyExtractors.toMono(ActiveService.class)).flatMap(activeService -> {
            final Optional<InetSocketAddress> requestAddress = request.remoteAddress();
            if(requestAddress.isPresent()) {
                activeService.setHost(requestAddress.get().getAddress().getHostAddress());
                routeService.add(activeService);
                inMemoryRegistry.add(activeService.getHost(), activeService);
                return ServerResponse.ok().bodyValue("");
            } else {
                return ServerResponse.badRequest().bodyValue("");
            }
        });
    }

    public Mono<ServerResponse> killService(ServerRequest request) {
        return request.body(BodyExtractors.toMono(ActiveService.class)).flatMap(activeService -> {
            final Optional<InetSocketAddress> requestAddress = request.remoteAddress();
            if(requestAddress.isPresent()) {
                activeService.setHost(requestAddress.get().getAddress().getHostAddress());
                inMemoryRegistry.remove(activeService.getHost());
                routeService.remove(activeService);
                return ServerResponse.ok().bodyValue("");
            } else {
                return ServerResponse.badRequest().bodyValue("");
            }
        });
    }

}