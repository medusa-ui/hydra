package io.getmedusa.hydra.core.config;

import io.getmedusa.hydra.core.security.HydraUser;
import io.getmedusa.hydra.core.security.JWTTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JWTAuthSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JWTTokenService jwtTokenService;

    public JWTAuthSuccessHandler(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        exchange.getResponse()
                .getHeaders()
                .add(HttpHeaders.AUTHORIZATION,
                        getHttpAuthHeaderValue(authentication));
        return webFilterExchange.getChain().filter(exchange);
    }
    private String getHttpAuthHeaderValue(Authentication authentication){
        return String.join(" ","Bearer", tokenFromAuthentication(authentication));
    }

    private String tokenFromAuthentication(Authentication authentication){
        HydraUser hydraUser = (HydraUser) authentication.getDetails();
        return jwtTokenService.generateToken(hydraUser);
    }
}
