package io.getmedusa.hydra.security.controller;

import io.getmedusa.hydra.security.controller.meta.LoginForm;
import io.getmedusa.hydra.security.service.JWTTokenService;
import io.getmedusa.hydra.security.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Controller
@ConditionalOnProperty("hydra.enable-security")
public class LoginController {

    private final JWTTokenService jwtTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(JWTTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
        this.passwordEncoder = userService.getPasswordEncoder();
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public Mono<String> processLogin(final ServerWebExchange exchange, final LoginForm loginForm) {
        return userService.findUserByUsername(loginForm.username()).flatMap(user -> {
            if(user == null)  return Mono.error(new SecurityException("Invalid login"));

            boolean isValid = passwordEncoder.matches(loginForm.password(), user.getPassword());
            if(!isValid) return Mono.error(new SecurityException("Invalid login"));

            exchange.getResponse().addCookie(ResponseCookie.from("HYDRA-SSO", jwtTokenService.generateToken(user)).build());

            return exchange.getSession()
                    .doOnNext(session -> userService.manualLogin(user, session))
                    .flatMap(WebSession::changeSessionId)
                    .then(Mono.just("redirect:/hydra/status"));
        });
    }
}
