package io.getmedusa.hydra.core.controller;

import io.getmedusa.hydra.core.security.JWTTokenService;
import io.getmedusa.hydra.core.service.UserService;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/login", file = "/pages/login.html")
public class LoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenService jwtTokenService;

    public LoginController(UserService userService, PasswordEncoder passwordEncoder, JWTTokenService jwtTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public record LoginForm(String username, String password, String _csrf) {}

    public void processLogin(LoginForm loginForm) {
        /*userService.findUserByUsername(loginForm.username()).flatMap(user -> {
            if(user == null) return Mono.error(new SecurityException("Invalid login"));

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
        }).subscribe();*/
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
