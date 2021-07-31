package io.getmedusa.hydra.discovery.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.discovery.service.RouteService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceController {

    private final RouteService routeService;
    private final InMemoryRegistry inMemoryRegistry;

    public static final ObjectMapper MAPPER = setupObjectMapper();

    public ServiceController(RouteService routeService, InMemoryRegistry inMemoryRegistry) {
        this.inMemoryRegistry = inMemoryRegistry;
        this.routeService = routeService;
    }

    /**
     * JSON mapper setup
     *
     * @return ObjectMapper
     */
    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        final Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/services/health", session -> session.send(Flux.empty())
                .log()
                .and(session.receive()
                        .map(m -> this
                                .handleIncomingHealthCheck(session, m.getPayloadAsText())
                                .subscribe())
                        .doFinally(x -> this.killService(session.getId()))
                ));
        return setupURLMapping(map);
    }

    private void killService(String sessionId) {
        ActiveService activeService = inMemoryRegistry.getAndRemove(sessionId);
        routeService.remove(activeService);
    }

    private SimpleUrlHandlerMapping setupURLMapping(Map<String, WebSocketHandler> map) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    private Mono<ActiveService> handleIncomingHealthCheck(WebSocketSession session, String payload) {
        final InetSocketAddress remoteAddress = session.getHandshakeInfo().getRemoteAddress();
        if (remoteAddress == null) return Mono.empty();
        return mapPayload(payload).flatMap(a -> {
            registerActiveService(session.getId(), remoteAddress, a);
            return Mono.just(a);
        });
    }

    private Mono<ActiveService> mapPayload(String payload) {
        return Mono.fromCallable(() -> {
            try {
                return MAPPER.readValue(payload, ActiveService.class);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void registerActiveService(String sessionId, InetSocketAddress remoteAddress, ActiveService a) {
        a.setHost(remoteAddress.getAddress().getHostAddress());
        routeService.add(a);
        inMemoryRegistry.add(sessionId, a);
    }
/*
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
*/
}