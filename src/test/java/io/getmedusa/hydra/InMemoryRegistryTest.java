package io.getmedusa.hydra;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class InMemoryRegistryTest {

    private final InMemoryRegistry registry = new InMemoryRegistry();

    @Test
    void testToURLList() {
        ActiveService activeService2 = new ActiveService();
        activeService2.setHost("127.0.0.222");
        activeService2.setPort(8081);
        activeService2.setName("serviceB");
        activeService2.setEndpoints(new HashSet<>(Arrays.asList("/page1", "/page2")));

        ActiveService activeService1 = new ActiveService();
        activeService1.setHost("127.0.0.111");
        activeService1.setPort(8080);
        activeService1.setName("serviceA");
        activeService1.setEndpoints(new HashSet<>(Arrays.asList("/page3", "/page4")));

        registry.add(UUID.randomUUID().toString(), activeService1);
        registry.add(UUID.randomUUID().toString(), activeService2);

        final Map<String, Set<String>> urlMap = registry.toURLMap();
        System.out.println(urlMap);
        Assertions.assertEquals(2, urlMap.size());
        for(String key : urlMap.keySet()) {
            Assertions.assertTrue("serviceA".equals(key) || "serviceB".equals(key));
        }
    }

}
