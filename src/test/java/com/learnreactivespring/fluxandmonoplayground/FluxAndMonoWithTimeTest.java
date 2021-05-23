package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {

        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))  // starts emitting Events from 0 to ...N
                .log();

        infiniteFlux.subscribe(e -> System.out.println("Value is +" + e));
        Thread.sleep(3000);
    }

    @Test
    public void finiteSequenceTest() throws InterruptedException {

        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();

    }

    @Test
    public void finiteSequenceMapTest() throws InterruptedException {

        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l-> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();

    }

    @Test
    public void finiteSequenceMap_WithDelay() throws InterruptedException {

        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l-> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();

    }
}
