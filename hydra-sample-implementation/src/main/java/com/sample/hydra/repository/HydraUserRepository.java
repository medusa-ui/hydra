package com.sample.hydra.repository;

import io.getmedusa.hydra.security.domain.HydraUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface HydraUserRepository extends ReactiveCrudRepository<HydraUser, Long> {

    @Query("SELECT * FROM hydra_user WHERE username = :#{[0]} limit 1")
    Mono<HydraUser> findByUsername(String username);

}
