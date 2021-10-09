package io.getmedusa.hydra;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.MenuItem;
import io.getmedusa.hydra.discovery.registry.InMemoryRegistry;
import io.getmedusa.hydra.discovery.registry.KnownRoute;
import io.getmedusa.hydra.discovery.registry.KnownRoutes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.*;

class InMemoryRegistryTest {

    private InMemoryRegistry registry;

    @BeforeEach
    void clear() {
        registry = new InMemoryRegistry();
    }

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

    private MenuItem buildMenu(String endpoint) {
        final MenuItem item = new MenuItem();
        item.setLabel(StringUtils.capitalize(endpoint.substring(1).replace("-", " ")));
        item.setEndpoint(endpoint);
        return item;
    }

    private ActiveService buildService(String menuName, MenuItem ... menuItem) {
        MenuItem mainItem = menuItem[0];
        ActiveService activeService = new ActiveService();
        activeService.setHost("127.0.0." + ((int)mainItem.getLabel().substring(mainItem.getLabel().length()-1).toCharArray()[0]));
        activeService.setPort(8080);
        activeService.setName(mainItem.getLabel().replace(" ", ""));
        activeService.setEndpoints(Set.of(mainItem.getEndpoint()));
        activeService.getMenuItems().put(menuName, Set.of(menuItem));
        return activeService;
    }

    @Test
    void testMenusSimpleAdditionRemovalOfSameMenu() {
        final String menuName = "top-menu";

        final MenuItem itemA = buildMenu("/service-a");
        ActiveService activeServiceA = buildService(menuName, itemA);
        String sessionA = UUID.randomUUID().toString();

        final MenuItem itemB = buildMenu("/service-b");
        ActiveService activeServiceB = buildService(menuName, itemB);
        String sessionB = UUID.randomUUID().toString();

        registry.add(sessionA, activeServiceA);
        registry.add(sessionB, activeServiceB);

        System.out.println(menuName + ": " + registry.getMenuItems().get(menuName));

        Assertions.assertEquals(2, registry.getMenuItems().get(menuName).size());
        registry.getAndRemove(sessionB);

        System.out.println("Remove session B");
        System.out.println(menuName + ": " + registry.getMenuItems().get(menuName));

        Assertions.assertEquals(1, registry.getMenuItems().get(menuName).size());
    }

    @Test
    void testMenusAddRemovalDifferentMenus() {
        final String menuName1 = "menu-1";
        final String menuName2 = "menu-2";

        final MenuItem unrelated = buildMenu("/unrelated");
        final MenuItem itemA = buildMenu("/service-a");

        ActiveService activeServiceA = buildService(menuName1, itemA);
        activeServiceA.getMenuItems().put(menuName2, Set.of(itemA, unrelated));
        String sessionA = UUID.randomUUID().toString();

        final MenuItem itemB = buildMenu("/service-b");
        ActiveService activeServiceB = buildService(menuName1, itemB);
        activeServiceB.getMenuItems().put(menuName2, Set.of(itemB, unrelated));
        String sessionB = UUID.randomUUID().toString();

        registry.add(sessionA, activeServiceA);
        registry.add(sessionB, activeServiceB);

        System.out.println(menuName1 + ": " + registry.getMenuItems().get(menuName1));
        System.out.println(menuName2 + ": " + registry.getMenuItems().get(menuName2));

        Assertions.assertEquals(2, registry.getMenuItems().get(menuName1).size());
        Assertions.assertEquals(3, registry.getMenuItems().get(menuName2).size());
        registry.getAndRemove(sessionB);
        System.out.println("Remove session B");

        System.out.println(menuName1 + ": " + registry.getMenuItems().get(menuName1));
        System.out.println(menuName2 + ": " + registry.getMenuItems().get(menuName2));

        Assertions.assertEquals(1, registry.getMenuItems().get(menuName1).size());
        Assertions.assertEquals(2, registry.getMenuItems().get(menuName2).size());
    }

    @Test
    void testMenuAddRemovalButOverlappingSessions() {
        final String menuName = "top-menu";

        final MenuItem itemA = buildMenu("/service-a");
        ActiveService activeServiceA = buildService(menuName, itemA);
        String sessionA = UUID.randomUUID().toString();

        final MenuItem itemB = buildMenu("/service-b");
        ActiveService activeServiceB1 = buildService(menuName, itemB);
        String sessionB1 = UUID.randomUUID().toString();

        ActiveService activeServiceB2 = buildService(menuName, itemB);
        activeServiceB2.setPort(9191);
        String sessionB2 = UUID.randomUUID().toString();

        registry.add(sessionA, activeServiceA);
        registry.add(sessionB1, activeServiceB1);
        registry.add(sessionB2, activeServiceB2);

        System.out.println(menuName + ": " + registry.getMenuItems().get(menuName));

        Assertions.assertEquals(2, registry.getMenuItems().get(menuName).size());
        registry.getAndRemove(sessionB1);

        System.out.println("Remove session B1");
        System.out.println(menuName + ": " + registry.getMenuItems().get(menuName));

        Assertions.assertEquals(2, registry.getMenuItems().get(menuName).size());

        registry.getAndRemove(sessionB2);
        System.out.println("Remove session B2");
        System.out.println(menuName + ": " + registry.getMenuItems().get(menuName));

        Assertions.assertEquals(1, registry.getMenuItems().get(menuName).size());
    }

}
