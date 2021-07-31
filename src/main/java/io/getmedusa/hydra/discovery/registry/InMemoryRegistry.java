package io.getmedusa.hydra.discovery.registry;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryRegistry {

    private Map<String, ActiveService> serviceMap = new HashMap<>();

    public Map<String, ActiveService> getServiceMap() {
        return serviceMap;
    }

    public void add(String host, ActiveService activeService) {
        System.out.println("Incoming registration from: " + host + ":" + activeService.getPort() + " w/ endpoints: " + activeService.getEndpoints().size());
        this.serviceMap.put(host + ":" + activeService.getPort(), activeService);
    }

    public void remove(String host) {
        System.out.println("Incoming kill from: " + host);
        this.serviceMap.remove(host);
    }
}
