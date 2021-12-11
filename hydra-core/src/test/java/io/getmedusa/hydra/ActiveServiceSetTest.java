package io.getmedusa.hydra;

import io.getmedusa.hydra.discovery.model.ActiveService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class ActiveServiceSetTest {

    @Test
    void activeServiceTest() {
        ActiveService a1 = new ActiveService();
        a1.setHost("localhost");
        a1.setName("A1");
        a1.setEndpoints(Set.of("/sample-a"));

        ActiveService a2 = new ActiveService();
        a2.setHost("localhost");
        a2.setName("A1");
        a2.setEndpoints(Set.of("/sample-a"));

        Set<ActiveService> set = new HashSet<>();
        set.add(a1);
        set.add(a2);
        Assertions.assertEquals(1, set.size());

    }

}
