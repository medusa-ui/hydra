package io.getmedusa.hydra.security;

import io.getmedusa.hydra.discovery.controller.ServiceController;
import io.getmedusa.hydra.security.domain.HydraUser;
import io.getmedusa.hydra.security.service.JWTTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JWTTokenServiceTest {

    private JWTTokenService service;

    @BeforeEach
    void setup() {
        service = new JWTTokenService(Mockito.mock(ServiceController.class));
    }

    @Test
    void testNewToken() {
        HydraUser hydraUser = new HydraUser();
        hydraUser.setUsername("user1234");
        hydraUser.setEnabled(true);

        final String token = service.generateToken(hydraUser);
        System.out.println(token);

        String[] tokenSplit = token.split("\\.");
        Assertions.assertEquals(3, tokenSplit.length);
        Assertions.assertTrue(service.verifyToken(token));
        service.cycleKeys();
        Assertions.assertFalse(service.verifyToken(token));
    }

}
