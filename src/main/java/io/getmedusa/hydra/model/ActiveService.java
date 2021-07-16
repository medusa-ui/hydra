package io.getmedusa.hydra.model;

import java.util.HashSet;
import java.util.Set;

public class ActiveService {

    private String host;
    private int port;
    private String name;
    private Set<String> endpoints = new HashSet<>();

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

    @Override
    public String toString() {
        return "ActiveService{" +
                "port=" + port +
                ", name='" + name + '\'' +
                ", endpoints=" + endpoints +
                '}';
    }
}
