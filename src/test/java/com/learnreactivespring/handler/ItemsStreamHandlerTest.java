/*
package com.learnreactivespring.handler;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.learnreactivespring.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemsStreamHandlerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
                .map(i -> new ItemCapped(null, "Random Item" + i, (100.00 + i)))
                .take(5);

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted item is :" + itemCapped);
                }).blockLast();
    }

    @Test
    public void testStreamAllItems() {
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ITEM_STREAM_FUNCTIONAL_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectSubscription()
                .expectNextCount(5)
                .thenCancel();
    }

}
*/
