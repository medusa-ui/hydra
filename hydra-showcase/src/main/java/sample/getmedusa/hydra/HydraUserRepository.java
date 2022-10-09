package sample.getmedusa.hydra;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class HydraUserRepository {

    private HydraUser user = null;

    public Mono<UserDetails> findByUsername(String username) {
        return Mono.just(user);
    }

    public void save(HydraUser user) {
        this.user = user;
    }
}
