package io.getmedusa.hydra.security.controller;

import io.getmedusa.hydra.security.controller.meta.LoginForm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
@ConditionalOnProperty("hydra.enable-security")
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public Mono<String> processLogin(LoginForm loginForm) {
        System.out.println(loginForm);
        return Mono.just("redirect:/login");
    }

}
