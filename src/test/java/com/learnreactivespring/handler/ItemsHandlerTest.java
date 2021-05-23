package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT;
import static com.learnreactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemsHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("item inserted is :" + item))
                .blockLast();
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "Apple watch", 299.99),
                new Item(null, "Beats Headphones", 149.9),
                new Item("ABC", "Boat Headphones", 220.34));
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item ->
                            Assertions.assertTrue(item.getId() != null));
                });
    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemsFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("value from network :"))
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 220.34);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "Iphone X", 999.99);
        webTestClient.post().uri(ITEM_FUNCTIONAL_END_POINT_V1).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem() {
        webTestClient.delete().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        double newPrice = 123.89;
        Item item = new Item(null, "Boat Headphones", newPrice);
        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(123.89);
    }

    @Test
    public void updateItem_notFound() {
        double newPrice = 123.89;
        Item item = new Item(null, "Boat Headphones", newPrice);
        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runtimeException() {
        webTestClient.get().uri("/fun/runtimeexception")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message", "Runtime Exception occurred");
    }
}
