package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring webflux")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("After error"))
                .log();

        stringFlux.subscribe(System.out::println,
                (e) -> System.err.println("Exception is :" + e),
                () -> System.out.println("Completed"));

    }


    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring webflux")
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Spring webflux")
                .verifyComplete();

    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring webflux")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Spring webflux")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred")
                .verify();

    }

    @Test
    public void fluxTestElements_WithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring webflux")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Spring webflux")
                .expectErrorMessage("Exception occurred")
                .verify();

    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring webflux")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();

    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
