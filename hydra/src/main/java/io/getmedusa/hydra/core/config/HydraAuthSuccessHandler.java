package io.getmedusa.hydra.core.config;

import io.getmedusa.hydra.core.security.HydraUser;
import io.getmedusa.hydra.core.security.JWTTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.RequestPath;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;

public class HydraAuthSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    private final JWTTokenService jwtTokenService;

    private static final URI ROOT = URI.create("/");
    private static final List<String> DEFAULT = List.of("/");

    public HydraAuthSuccessHandler(JWTTokenService jwtTokenService) {
        super();
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        HydraUser hydraUser = (HydraUser) authentication.getPrincipal();
        exchange.getResponse().addCookie(
                ResponseCookie.from("HYDRA-SSO", jwtTokenService.generateToken(hydraUser))
                        .httpOnly(true)
                        .maxAge(Duration.ofHours(12))
                        .build());

        setLocationToReferer(exchange.getRequest().getPath(), exchange.getRequest().getHeaders());

        return super.onAuthenticationSuccess(webFilterExchange, authentication);
    }

    private void setLocationToReferer(RequestPath path, HttpHeaders headers) {
        if (headers != null && path != null) {
            String referer = headers.getOrDefault("Referer", DEFAULT).get(0);
            if(referer.contains("?")) {
                referer = referer.split("\\?")[0];
            }

            if(referer.endsWith(path.toString())) {
                setLocation(ROOT);
            } else {
                setLocation(URI.create(referer));
            }
        }
    }
}
