package io.getmedusa.hydra.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JWTTokenServiceTest {

    private final JWTTokenService service = new JWTTokenService();

    @Test
    void testNewToken() {
        final String token = service.generateToken();
        System.out.println(token);

        String[] tokenSplit = token.split("\\.");
        Assertions.assertEquals(3, tokenSplit.length);
        Assertions.assertTrue(service.verifyToken(token));
        service.cycleKeys();
        Assertions.assertFalse(service.verifyToken(token));
    }

}
