package io.getmedusa.hydra.core.controller;

import io.getmedusa.hydra.core.controller.meta.LoginForm;
import io.getmedusa.hydra.core.security.JWTTokenService;
import io.getmedusa.hydra.core.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Controller
public class LoginController {

    private final JWTTokenService jwtTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(JWTTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
        this.passwordEncoder = userService.getPasswordEncoder();
    }

    @Bean
    public RouterFunction<ServerResponse> loginRoute() {
        return route(POST("/login"), request -> process(request.exchange()));
    }

    private Mono<ServerResponse> process(ServerWebExchange exchange) {
        return exchange.getFormData().map(formData -> {
            final Map<String, String> map = formData.toSingleValueMap();
            return new LoginForm(map.get("username"), map.get("password"), map.get("_csrf"));
        }).flatMap(loginForm -> processLogin(exchange, loginForm));
    }

    public Mono<ServerResponse> processLogin(final ServerWebExchange exchange, final LoginForm loginForm) {
        return userService.findUserByUsername(loginForm.username()).flatMap(user -> {
            if(user == null)  return Mono.error(new SecurityException("Invalid login"));

            boolean isValid = passwordEncoder.matches(loginForm.password(), user.getPassword());
            if(!isValid) return Mono.error(new SecurityException("Invalid login"));

            exchange.getResponse().addCookie(
                    ResponseCookie.from("HYDRA-SSO", jwtTokenService.generateToken(user))
                            .httpOnly(true)
                            .maxAge(Duration.ofHours(12))
                            .build());

            String referer = findReferred(exchange);
            return exchange.getSession()
                    .doOnNext(session -> userService.manualLogin(user, session))
                    .flatMap(WebSession::changeSessionId)
                    .then(ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue("<head><meta http-equiv=\"Refresh\" content=\"0; URL="+referer+"\"></head>"));
        });
    }

    private String findReferred(ServerWebExchange exchange) {
        final List<HttpCookie> refererCookies = exchange.getRequest().getCookies().getOrDefault("Referer", new ArrayList<>());
        String referer = "";
        if(null != refererCookies && !refererCookies.isEmpty()) {
            referer = refererCookies.get(0).getValue();
        }
        exchange.getResponse().addCookie(ResponseCookie.from("Referer", "").maxAge(0).build());
        return Jsoup.clean(referer, Safelist.none());
    }

}
