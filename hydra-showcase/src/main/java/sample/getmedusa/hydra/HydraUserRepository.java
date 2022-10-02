package sample.getmedusa.hydra;

import io.getmedusa.hydra.core.domain.HydraUser;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class HydraUserRepository {

    public Mono<HydraUser> findByUsername(String username) {
        HydraUser user = new HydraUser();
        user.setUsername(username);
        return Mono.just(user);
    }
}
