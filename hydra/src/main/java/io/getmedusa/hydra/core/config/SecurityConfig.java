package io.getmedusa.hydra.core.config;

import io.getmedusa.hydra.core.security.JWTTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private final JWTTokenService jwtTokenService;

    public SecurityConfig(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //http.
        return http.authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .formLogin(Customizer.withDefaults())
                .httpBasic().disable()
                .build();
    }
}
