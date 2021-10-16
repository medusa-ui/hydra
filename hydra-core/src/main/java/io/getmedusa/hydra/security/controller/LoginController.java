package io.getmedusa.hydra.security.controller;

import io.getmedusa.hydra.security.JWTTokenService;
import io.getmedusa.hydra.security.controller.meta.LoginForm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@ConditionalOnProperty("hydra.enable-security")
public class LoginController {

    private final JWTTokenService jwtTokenService;

    public LoginController(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public Mono<String> processLogin(ServerWebExchange exchange, LoginForm loginForm) {
        System.out.println(loginForm);

        exchange.getResponse().addCookie(ResponseCookie.from("HYDRA-SSO", jwtTokenService.generateToken()).build());

        return Mono.just("redirect:/page2");
    }

}
