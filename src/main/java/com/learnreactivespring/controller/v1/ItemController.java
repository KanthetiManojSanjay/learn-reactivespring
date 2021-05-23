package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT)
    public Flux<Item> getAllItem() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(ITEM_END_POINT + "/runtimeException")
    public Flux<Item> runtimeException() {

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Runtime Exception occurred")));
    }

    @PostMapping(ITEM_END_POINT)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_END_POINT + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item, @PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                }).map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
