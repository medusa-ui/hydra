package io.getmedusa.hydra.core.config;

import io.getmedusa.hydra.core.security.JWTTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private final JWTTokenService jwtTokenService;

    private final ReactiveAuthenticationManager authenticationManager;

    public SecurityConfig(JWTTokenService jwtTokenService, ReactiveUserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    //login with 'hello' / 'world'
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, WebFilterChainServerAuthenticationSuccessHandler handler) {
        return http
                .addFilterAt(new LoginWebFilter(authenticationManager, handler), SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf().disable() //TODO enable + custom login page in showcase
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .formLogin(Customizer.withDefaults())
                .httpBasic().disable()
                .build();
    }
}
