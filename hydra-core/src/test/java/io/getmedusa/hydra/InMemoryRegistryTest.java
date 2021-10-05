package io.getmedusa.hydra;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.discovery.registry.KnownRoute;
import io.getmedusa.hydra.discovery.registry.KnownRoutes;
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

        final KnownRoutes urlMap = registry.toURLMap();
        Assertions.assertEquals(2, urlMap.getKnownRoutes().size());
        for(KnownRoute key : urlMap.getKnownRoutes()) {
            final String service = key.getService();
            Assertions.assertTrue("serviceA".equals(service) || "serviceB".equals(service));
        }
    }

}
