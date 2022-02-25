package io.getmedusa.hydra.discovery.model;

import java.time.Instant;
import java.util.*;

public class ActiveService {
    // TODO: will be overridden when ServiceController get initialised
    public static String webProtocol = "https";

    private String host;
    private int port;
    private String name;
    private String secret;
    private long version;
    private AwakeningType awakening;
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
                ", version=" + version +
                ", endpoints=" + endpoints +
                '}';
    }

    public String toBaseURI() {
        return String.format("%s://%s:%s",webProtocol,getHost(),getPort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActiveService that)) return false;
        return Objects.equals(getPort(), that.getPort()) &&
                Objects.equals(getHost(), that.getHost()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort(), getName(), getVersion());
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public AwakeningType getAwakening() {
        return awakening;
    }

    public void setAwakening(AwakeningType awakening) {
        this.awakening = awakening;
    }
}
