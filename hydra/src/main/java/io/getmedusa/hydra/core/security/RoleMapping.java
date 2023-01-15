package io.getmedusa.hydra.core.security;

import java.util.ArrayList;
import java.util.List;

public class RoleMapping {

    private String mappedTo;
    private String service;

    public RoleMapping() {
    }

    public RoleMapping(String mappedTo, String service) {
        this.mappedTo = mappedTo;
        this.service = service;
    }

    public String getMappedTo() {
        return mappedTo;
    }

    public void setMappedTo(String mappedTo) {
        this.mappedTo = mappedTo;
    }

    public String getService() {
        return service;
    }

    public List<String> getServices() {
        final String[] split = service.split(",");
        List<String> services = new ArrayList<>();
        for(String s : split) services.add(s.trim());
        return services;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "RoleMapping{" +
                "mappedTo='" + mappedTo + '\'' +
                ", service='" + getServices() + '\'' +
                '}';
    }
}
