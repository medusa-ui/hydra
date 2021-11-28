package io.getmedusa.hydra.security.service;

import io.getmedusa.hydra.security.domain.HydraUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Mono<HydraUser> findUserByUsername(String username);

    PasswordEncoder getPasswordEncoder();

    default void manualLogin(final HydraUser hydraUser, WebSession session) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(hydraUser.getUsername(), null, hydraUser.getAuthorities());
        securityContext.setAuthentication(authentication);
        session.getAttributes().put(WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
    }
}
