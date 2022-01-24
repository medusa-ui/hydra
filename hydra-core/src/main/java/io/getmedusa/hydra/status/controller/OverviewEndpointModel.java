package io.getmedusa.hydra.status.controller;

public record OverviewEndpointModel(String name, String ip, String version, String endpoint, int weight) implements Comparable<OverviewEndpointModel> {

    @Override
    public int compareTo(OverviewEndpointModel other) {
        return (name() + endpoint() + version()).compareTo(other.name() + other.endpoint() + other.version());
    }
}
