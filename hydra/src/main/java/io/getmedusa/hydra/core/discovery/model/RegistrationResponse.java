package io.getmedusa.hydra.core.discovery.model;

import java.util.List;

public class RegistrationResponse {

    private String publicKey;
    private List<ActiveServiceOverview> services;

    public RegistrationResponse(String publicKey, List<ActiveServiceOverview> services) {
        this.publicKey = publicKey;
        this.services = services;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<ActiveServiceOverview> getServices() {
        return services;
    }

    public void setServices(List<ActiveServiceOverview> services) {
        this.services = services;
    }
}
