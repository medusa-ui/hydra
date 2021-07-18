package io.getmedusa.hydra.status.model;

public class ServiceStatus {

    private String name;
    private long upSince;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpSince() {
        return upSince;
    }

    public void setUpSince(long upSince) {
        this.upSince = upSince;
    }
}
