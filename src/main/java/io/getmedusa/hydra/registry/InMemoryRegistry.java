package io.getmedusa.hydra.registry;

import io.getmedusa.hydra.model.ActiveService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryRegistry {

    private Map<String, ActiveService> serviceMap = new HashMap<>();

    public Map<String, ActiveService> getServiceMap() {
        return serviceMap;
    }

    public void clear() {
        this.serviceMap.clear();
    }

    public void add(String host, ActiveService activeService) {
        System.out.println("Incoming registration from: " + host + ":" + activeService.getPort());
        this.serviceMap.put(host + ":" + activeService.getPort(), activeService);
    }
}
