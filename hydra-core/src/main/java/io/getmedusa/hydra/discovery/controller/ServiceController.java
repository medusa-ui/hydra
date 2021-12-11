package io.getmedusa.hydra.discovery.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.discovery.service.RouteService;
import io.getmedusa.hydra.security.service.JWTTokenService;
import io.getmedusa.hydra.util.WebsocketMessageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class ServiceController {

    private final RouteService routeService;
    private final InMemoryRegistry inMemoryRegistry;
    private final String expectedSecret;

    public static final ObjectMapper MAPPER = setupObjectMapper();

    public ServiceController(RouteService routeService, InMemoryRegistry inMemoryRegistry, @Value("${hydra.web.protocol:https}") String protocol, @Value("${hydra.secret}") String secret) {
        this.inMemoryRegistry = inMemoryRegistry;
        this.routeService = routeService;
        this.expectedSecret = secret;

        ActiveService.webProtocol = protocol;
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


    Flux<WebSocketMessage> sendData = Flux.empty();

    //@Scheduled(fixedRate = 2000)
    public void openSessions() {
        System.out.println("Open sessions: " + activeSessions.size());
    }

    public void sendURLMapToAll() {
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("urlMap", inMemoryRegistry.toURLMap());
        dataToSend.put("menuItems", inMemoryRegistry.getMenuItems());
        this.sendData = Flux.just(WebsocketMessageUtils.fromObject(dataToSend));
        for(WebSocketSession session : activeSessions) {
            session.send(sendData).subscribe();
        }
    }

    public void sendPublicKey() {
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("pub-key", JWTTokenService.publicKeyAsString);
        this.sendData = Flux.just(WebsocketMessageUtils.fromObject(dataToSend));
        for(WebSocketSession session : activeSessions) {
            session.send(sendData).subscribe();
        }
    }

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
        sendURLMapToAll();
    }

    private SimpleUrlHandlerMapping setupURLMapping(Map<String, WebSocketHandler> map) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    private final Executor singleThreadExecutor = Executors.newSingleThreadExecutor();

    private Mono<ActiveService> handleIncomingHealthCheck(WebSocketSession session, String payload) {
        final InetSocketAddress remoteAddress = session.getHandshakeInfo().getRemoteAddress();
        if (remoteAddress == null) return Mono.empty();
        return mapPayload(payload).flatMap(a -> {
            if(expectedSecret.equals(a.getSecret())) {
                singleThreadExecutor.execute(() -> { //force this to be handled sequentially to prevent concurrency errors w/ routes
                    activeSessions.add(session);
                    registerActiveService(session.getId(), remoteAddress, a);
                    sendURLMapToAll();
                    sendPublicKey();
                });
                return Mono.just(a);
            } else {
                return Mono.error(new SecurityException("Non-matching hydra secret"));
            }
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