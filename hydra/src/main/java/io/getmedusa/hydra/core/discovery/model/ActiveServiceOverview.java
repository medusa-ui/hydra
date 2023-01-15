package io.getmedusa.hydra.core.discovery.model;

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActiveServiceOverview {

    private String serviceName;
    private Set<ActiveService> activeServices;

    public static RegistrationResponse of(Map<String, Set<ActiveService>> activeServicesMap, String publicKeyAsString, Map<String, String> roleMappings) {
        List<ActiveServiceOverview> list = new ArrayList<>();

        for(Map.Entry<String, Set<ActiveService>> entry : activeServicesMap.entrySet()) {
            ActiveServiceOverview overview = new ActiveServiceOverview();
            overview.setServiceName(entry.getKey());
            overview.setActiveServices(entry.getValue());
            list.add(overview);
        }

        return new RegistrationResponse(publicKeyAsString, list, roleMappings);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Set<ActiveService> getActiveServices() {
        return activeServices;
    }

    public void setActiveServices(Set<ActiveService> activeServices) {
        this.activeServices = activeServices;
    }
}
