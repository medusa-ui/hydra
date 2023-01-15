package io.getmedusa.hydra.core.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class RoleMappingPropertiesTest {

    protected static final String MY_SERVICE = "MyService";
    protected static final String ADMIN = "ADMIN";

    private RoleMappingProperties buildProperties(String service) {
        RoleMappingProperties p = new RoleMappingProperties();
        p.setRoles(Map.of("MANAGER", new RoleMapping(ADMIN, service + ", XYZ")));
        return p;
    }

    private String test(String serviceNameStoredInProperties) {
        RoleMappingProperties p = buildProperties(serviceNameStoredInProperties);
        Map<String, String> roleMapping = p.findByService(MY_SERVICE);
        return roleMapping.getOrDefault("MANAGER", null);
    }

    @Test
    void testServiceMatchingDirectMatch() {
        Assertions.assertEquals(ADMIN, test("MyService"));
    }

    @Test
    void testServiceMatchingPartialMatch() {
        Assertions.assertEquals(ADMIN, test("MyS.*"));
    }

    @Test
    void testServiceMatchingNoMatch() {
        Assertions.assertNull(test("XYZ"));
    }

    @Test
    void testServiceMatchingWildcardMatch() {
        Assertions.assertEquals(ADMIN, test(".*"));
    }

}
