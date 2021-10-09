package io.getmedusa.hydra.discovery.model;

import java.time.Instant;
import java.util.*;

public class ActiveService {

    private String host;
    private int port;
    private String name;
    private Set<String> endpoints = new HashSet<>();
    private Set<String> websockets = new HashSet<>();
    private Set<String> staticResources = new HashSet<>();
    private Map<String, Set<MenuItem>> menuItems = new HashMap<>();

    private final long activeSince;

    public ActiveService() {
        this.activeSince = Instant.now().toEpochMilli();
    }

    public long getActiveSince() {
        return activeSince;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Set<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Set<String> endpoints) {
        this.endpoints = endpoints;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getWebsockets() {
        return websockets;
    }

    public void setWebsockets(Set<String> websockets) {
        this.websockets = websockets;
    }

    public Set<String> getStaticResources() {
        return staticResources;
    }

    public void setStaticResources(Set<String> staticResources) {
        this.staticResources = staticResources;
    }

    public Map<String, Set<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Map<String, Set<MenuItem>> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public String toString() {
        return "ActiveService{" +
                "port=" + port +
                ", name='" + name + '\'' +
                ", endpoints=" + endpoints +
                '}';
    }

    public String toBaseURI() {
        return ProtocolDecider.getWebProtocol(getHost()) + getHost() + ":" + getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActiveService)) return false;
        ActiveService that = (ActiveService) o;
        return getPort() == that.getPort() && getHost().equals(that.getHost()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort(), getName());
    }

    private static class ProtocolDecider {

        static String getWebProtocol(String host) {
            if(isLocalHost(host)) return "http://";
            return "https://";
        }

        static String getWebSocketProtocol(String host) {
            if(isLocalHost(host)) return "ws://";
            return "wss://";
        }

        private static boolean isLocalHost(String host) {
            return "localhost".equals(host) || "127.0.0.1".equals(host);
        }


    }
}
