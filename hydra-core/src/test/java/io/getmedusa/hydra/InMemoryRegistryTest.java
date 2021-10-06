package io.getmedusa.hydra;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.MenuItem;
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

    @Test
    void testMenusWithMultipleSessions() {
        final MenuItem item = new MenuItem();
        item.setEndpoint("/123");
        item.setLabel("My label");
        Map<String, List<MenuItem>> menuItemsA = Map.of("top-menu", Collections.singletonList(item));

        final MenuItem item2 = new MenuItem();
        item2.setEndpoint("/543");
        item.setLabel("Other label");
        Map<String, List<MenuItem>> menuItemsB = Map.of("bottom-menu", Collections.singletonList(item2));

        ActiveService activeServiceA1 = new ActiveService();
        activeServiceA1.setHost("127.0.0.111");
        activeServiceA1.setPort(8080);
        activeServiceA1.setName("serviceA");
        activeServiceA1.setEndpoints(new HashSet<>(Arrays.asList("/page3", "/page4")));
        activeServiceA1.setMenuItems(menuItemsA);

        ActiveService activeServiceA2 = new ActiveService();
        activeServiceA2.setHost("127.0.0.112");
        activeServiceA2.setPort(8080);
        activeServiceA2.setName("serviceA");
        activeServiceA2.setEndpoints(new HashSet<>(Arrays.asList("/page3", "/page4")));
        activeServiceA2.setMenuItems(menuItemsA);

        ActiveService activeServiceB = new ActiveService();
        activeServiceB.setHost("127.0.0.50");
        activeServiceB.setPort(8081);
        activeServiceB.setName("serviceB");
        activeServiceB.setEndpoints(new HashSet<>(List.of("/page")));
        activeServiceB.setMenuItems(menuItemsB);

        String sessionA1 = UUID.randomUUID().toString();
        String sessionA2 = UUID.randomUUID().toString();
        String sessionB = UUID.randomUUID().toString();

        registry.add(sessionA1, activeServiceA1);
        registry.add(sessionA2, activeServiceA2);
        registry.add(sessionB, activeServiceB);

        Assertions.assertEquals(2, registry.getMenuItems().size());
        registry.getAndRemove(sessionA2);
        Assertions.assertEquals(2, registry.getMenuItems().size());
        registry.getAndRemove(sessionA1);
        Assertions.assertEquals(1, registry.getMenuItems().size());
        registry.getAndRemove(sessionB);
        Assertions.assertEquals(0, registry.getMenuItems().size());
    }

}
