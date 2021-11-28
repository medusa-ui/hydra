package com.sample.hydra.service;

import com.sample.hydra.repository.HydraUserRepository;
import io.getmedusa.hydra.security.domain.HydraUser;
import io.getmedusa.hydra.security.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HydraUserService implements UserService {

    private final HydraUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public HydraUserService(HydraUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Mono<HydraUser> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
