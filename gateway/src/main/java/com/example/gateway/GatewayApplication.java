package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Predicate;

@SpringBootApplication
public class GatewayApplication {

    // netflix zuul
    // spring cloud netflix zuul
    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(rs -> rs
                        .path("/")
                        .filters(fs ->
                                fs
//                                fs.requestRateLimiter( rt -> rt.)
                                        .jsonToGRPC(null, null, null, null)
                                        .filter((exchange, chain) -> chain.filter(exchange))
                        )
                        .uri("http://www.adobe.com")
                )
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
