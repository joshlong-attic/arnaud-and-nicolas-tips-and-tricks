package com.example.java21;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Java21Application {

    public static void main(String[] args) {
        SpringApplication.run(Java21Application.class, args);
    }

    static class Cat {
        Cat() {
            System.out.println("meow");
        }
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            Cat c = from();
        };
    }

    <T> T from(@Deprecated T... ts) throws Exception {
        var l = ts.length;
        var t = ts.getClass().getComponentType();
        System.out.println("component type is " + t.getName());
        return (T) t.getDeclaredConstructor().newInstance();
    }
}

// brian goetz on data oriented programming

// records
// pattern matching
// smart switch expressions
// sealed types

