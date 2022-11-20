package io.getmedusa.hydra.core.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

public interface HydraUserService extends ReactiveUserDetailsService {

    Mono<HydraUser> findUserByUsername(String username);

    PasswordEncoder getPasswordEncoder();

    default Mono<UserDetails> findByUsername(String username) {
        return findUserByUsername(username).cast(UserDetails.class);
    }

}