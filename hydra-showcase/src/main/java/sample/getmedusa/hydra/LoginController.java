package sample.getmedusa.hydra;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class LoginController {

    //https://github.com/medusa-ui/hydra/blob/7b424c0bb70457499514fae89e325e970bbd5edc/hydra-core/src/main/java/io/getmedusa/hydra/security/controller/LoginController.java#L63

    @GetMapping("/login")
    public Mono<String> login(){
        return Mono.just("login");
    }

}
