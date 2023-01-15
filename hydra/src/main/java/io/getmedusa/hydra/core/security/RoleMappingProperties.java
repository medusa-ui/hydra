package io.getmedusa.hydra.core.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "medusa.hydra")
public class RoleMappingProperties {

    private Map<String, RoleMapping> roles = new HashMap<>();

    public Map<String, RoleMapping> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, RoleMapping> roles) {
        this.roles = roles;
    }

    public Map<String, String> findByService(String name) {
        final Map<String, String> relevantRoles = new HashMap<>();
        for(var entrySet : getRoles().entrySet()) {
            final RoleMapping service = entrySet.getValue();
            for(String regex : service.getServices()) {
                if(name.matches(regex)) {
                    relevantRoles.put(entrySet.getKey(), service.getMappedTo());
                }
            }
        }
        return relevantRoles;
    }
}
