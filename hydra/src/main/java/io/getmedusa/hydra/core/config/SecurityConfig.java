package io.getmedusa.hydra.core.config;

import io.getmedusa.hydra.core.security.JWTTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.SAME_ORIGIN;
import static org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private final JWTTokenService jwtTokenService;

    public SecurityConfig(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    //login with 'hello' / 'world'
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.mode(SAMEORIGIN))
                        .referrerPolicy(SAME_ORIGIN))
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .formLogin(form -> form.authenticationSuccessHandler(new HydraAuthSuccessHandler(jwtTokenService))) //TODO make redirect either a property or dynamic based on referer
                .csrf().disable() //TODO re-enable this when using gRPC instead of REST for medusa calls
                .build();
    }
}
