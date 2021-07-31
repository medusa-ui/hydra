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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @return ObjectMapper
     */
    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private final List<WebSocketSession> activeSessions = new ArrayList<>();

    /*
    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
    Flux<WebSocketMessage> sendData = Flux.empty();

    @Scheduled(fixedRate = 2000)
    public void openSessions() {
        System.out.println("Open sessions: " + activeSessions.size());
    }

    public void sendAMessage() {
        byte[] bytes = "Data of services active right now".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = dataBufferFactory.wrap(bytes);
        WebSocketMessage m = new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer);
        this.sendData = Flux.just(m);
        for(WebSocketSession session : activeSessions) {
            System.out.println("Sending data ...");
            session.send(sendData);
        }
    }
    */

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        final Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/services/health", session -> session.send(Flux.empty())
                .and(session.receive()
                        .map(m -> this
                                .handleIncomingHealthCheck(session, m.getPayloadAsText())
                                .subscribe())
                        .doFinally(x -> this.killService(session))
                ));
        return setupURLMapping(map);
    }

    private void killService(WebSocketSession session) {
        session.close().subscribe();
        activeSessions.remove(session);
        ActiveService activeService = inMemoryRegistry.getAndRemove(session.getId());
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
            activeSessions.add(session);
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

}