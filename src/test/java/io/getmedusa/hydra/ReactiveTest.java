package io.getmedusa.hydra;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ReactiveTest {

    @Test
    void testReactiveStuff() {
        Publisher<Integer> range = Flux.range(1, 10);
        StepVerifier.create(range).expectNextCount(10).verifyComplete();
    }

}
