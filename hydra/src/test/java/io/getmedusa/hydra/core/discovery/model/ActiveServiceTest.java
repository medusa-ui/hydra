package io.getmedusa.hydra.core.discovery.model;

import io.getmedusa.hydra.core.discovery.model.meta.ActiveService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class ActiveServiceTest {

    @Test
    void testSet() {
        Set<ActiveService> activeServices = new HashSet<>();

        activeServices.add(randomService(1));
        activeServices.add(randomService(1));
        activeServices.add(randomService(1));

        Assertions.assertEquals(1, activeServices.size());

        activeServices.add(randomService(2));
        activeServices.add(randomService(1));

        Assertions.assertEquals(2, activeServices.size());
    }

    private ActiveService randomService(int i) {
        ActiveService a = new ActiveService();
        a.setHost("10.214.15.17" + i);
        a.setSocketPort(0);
        a.setPort(10000);
        a.setWebProtocol("http");
        return a;
    }

}
