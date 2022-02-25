package io.getmedusa.hydra.discovery.service;

import io.getmedusa.hydra.discovery.model.ActiveService;
import io.getmedusa.hydra.discovery.model.AwakeningType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class WeightServiceTest {

    protected static final String ENDPOINT = "/abc";
    private final WeightService weightService = new WeightService();

    @Test
    void testWeightNew() {
        final ActiveService activeService = buildActiveService(AwakeningType.EQUAL_SHARE);
        weightService.load(Set.of(activeService));
        Assertions.assertEquals(10,  weightService.generateWeight(ENDPOINT, activeService));
    }

    // 3 instances w/ 3 versions = 33% of traffic each
    @Test
    void testMultipleEqualShare_Stages() {
        final ActiveService activeServiceA = buildActiveService(AwakeningType.EQUAL_SHARE);

        weightService.load(Set.of(activeServiceA));
        Assertions.assertEquals(10, weightService.generateWeight(ENDPOINT, activeServiceA));


        final ActiveService activeServiceB = buildActiveService(AwakeningType.EQUAL_SHARE);
        weightService.load(Set.of(activeServiceA, activeServiceB));

        Assertions.assertEquals(5, weightService.generateWeight(ENDPOINT, activeServiceA));
        Assertions.assertEquals(5, weightService.generateWeight(ENDPOINT, activeServiceB));


        final ActiveService activeServiceC = buildActiveService(AwakeningType.EQUAL_SHARE);
        weightService.load(Set.of(activeServiceA, activeServiceB, activeServiceC));

        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceA));
        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceB));
        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceC));
    }

    // 3 instances w/ 3 versions = 33% of traffic each
    @Test
    void testMultipleEqualShare_All() {
        final ActiveService activeServiceA = buildActiveService(AwakeningType.EQUAL_SHARE);
        final ActiveService activeServiceB = buildActiveService(AwakeningType.EQUAL_SHARE);
        final ActiveService activeServiceC = buildActiveService(AwakeningType.EQUAL_SHARE);

        weightService.load(Set.of(activeServiceA, activeServiceB, activeServiceC));

        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceA));
        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceB));
        Assertions.assertEquals(3, weightService.generateWeight(ENDPOINT, activeServiceC));
    }

    // default - 3 instances w/ 2 versions, v1 (1, 0% traffic) and v2(2; each 50% traffic)
    @Test
    void testNewestVersionWins() {
        Set<ActiveService> activeServices = new HashSet<>();
        final ActiveService oldVersion = buildActiveService(AwakeningType.NEWEST_VERSION_WINS, 123L);
        activeServices.add(oldVersion);

        weightService.load(activeServices);
        Assertions.assertEquals(10, weightService.generateWeight(ENDPOINT, oldVersion));

        final ActiveService newVersionA = buildActiveService(AwakeningType.NEWEST_VERSION_WINS, 500L);
        activeServices.add(newVersionA);

        weightService.load(activeServices);
        Assertions.assertEquals(0, weightService.generateWeight(ENDPOINT, oldVersion));
        Assertions.assertEquals(10, weightService.generateWeight(ENDPOINT, newVersionA));

        final ActiveService newVersionB = buildActiveService(AwakeningType.NEWEST_VERSION_WINS, 500L);
        activeServices.add(newVersionB);

        weightService.load(activeServices);
        Assertions.assertEquals(0, weightService.generateWeight(ENDPOINT, oldVersion));
        Assertions.assertEquals(5, weightService.generateWeight(ENDPOINT, newVersionA));
        Assertions.assertEquals(5, weightService.generateWeight(ENDPOINT, newVersionB));
    }

    private ActiveService buildActiveService(AwakeningType type) {
        final ActiveService activeService = new ActiveService();
        activeService.setAwakening(type);
        activeService.setHost("https://getmedusa.io");
        activeService.setName("ABC");
        activeService.setPort(new SecureRandom().nextInt(9999));
        activeService.setSecret(UUID.randomUUID().toString());
        activeService.setVersion(1234L);
        activeService.setEndpoints(Set.of(ENDPOINT));
        return activeService;
    }

    private ActiveService buildActiveService(AwakeningType type, Long version) {
        final ActiveService activeService = new ActiveService();
        activeService.setAwakening(type);
        activeService.setHost("https://getmedusa.io");
        activeService.setName("ABC");
        activeService.setVersion(version);
        activeService.setPort(new SecureRandom().nextInt(9999));
        activeService.setSecret(UUID.randomUUID().toString());
        activeService.setEndpoints(Set.of(ENDPOINT));
        return activeService;
    }

}
