package io.getmedusa.hydra.core.discovery.model;

import java.util.List;
import java.util.Map;

public class RegistrationResponse {

    private String publicKey;
    private List<ActiveServiceOverview> services;
    private Map<String, String> roleMappings;

    public RegistrationResponse(String publicKey, List<ActiveServiceOverview> services, Map<String, String> roleMappings) {
        this.publicKey = publicKey;
        this.services = services;
        this.roleMappings = roleMappings;
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

    public Map<String, String> getRoleMappings() {
        return roleMappings;
    }

    public void setRoleMappings(Map<String, String> roleMappings) {
        this.roleMappings = roleMappings;
    }
}
