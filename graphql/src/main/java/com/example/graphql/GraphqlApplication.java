package com.example.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@SpringBootApplication
public class GraphqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }

}

@Controller
class CustomerController {

    private final Set<Customer> db = Set.of(
            new Customer(1, "Arnaud"),
            new Customer(2, "Nicolas"),
            new Customer(3, "Josh")
    );

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customerList) {
        var map = new HashMap<Customer, Profile>();
        for (var c : customerList) map.put(c, new Profile(c.id()));
        System.out.println("returning All the profiles for customers [" + customerList +
                "]");
        return map;
    }
/*
    @SchemaMapping(typeName = "Customer")
    Mono<Profile> profile(Customer customer) throws InterruptedException {
        System.out.println("calling network service for 1:1 profile ");
        // todo call profile service for customerId == ...
        return Mono.just(new Profile(customer.id())).delayElement(Duration.ofSeconds(2));
    }*/

    @QueryMapping
    Collection<Customer> customers() {
        return this.db;
    }

    @QueryMapping
    Collection<Customer> customersByName(@Argument String name) {
        return this.db.stream()
                .filter(customer -> customer.name().contains(name)).toList();
    }

}

record Profile(Integer id) {
}

record Customer(Integer id, String name) {
}