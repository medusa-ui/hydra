package io.getmedusa.hydra.security.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class HydraUser implements UserDetails {

    private Long id;
    private String encodedPassword;
    private String username;
    private String roles;
    private Map<String, String> additionalMetadata;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, String> getAdditionalMetadata() {
        return additionalMetadata;
    }

    public Long getId() {
        return id;
    }

    public List<String> getRoles() {
        if(roles == null || roles.isBlank()) return new ArrayList<>();
        return Arrays.asList(roles.split(","));
    }
}
