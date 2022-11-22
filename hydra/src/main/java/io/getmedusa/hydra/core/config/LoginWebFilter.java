package io.getmedusa.hydra.core.config;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

public class LoginWebFilter extends AuthenticationWebFilter {

    public LoginWebFilter(ReactiveAuthenticationManager authenticationManager, WebFilterChainServerAuthenticationSuccessHandler handler) {
        super(authenticationManager);
        setAuthenticationSuccessHandler(handler);
    }

}
