package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple watch", 299.99),
            new Item(null, "Beats Headphones", 149.9),
            new Item("ABC", "Boat Headphones", 220.34)
    );

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is :" + item))
                .blockLast();
    }


    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Boat Headphones"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Apple watch").log("findByDescription :"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);
        StepVerifier.create(savedItem.log("saveItem :"))
                .expectSubscription()
                .expectNextMatches(item1 -> (item.getId() != null && item.getDescription().equals("Google Home Mini")))
                .verifyComplete();
    }

    @Test
    public void updateItemPrice() {
        double newPrice = 520.99;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);  // setting the new price
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item); // save the item with newPrice in db
                });

        StepVerifier.create(updatedItem.log("updatedItem :"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.99)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                });
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List :"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List :"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }

}
