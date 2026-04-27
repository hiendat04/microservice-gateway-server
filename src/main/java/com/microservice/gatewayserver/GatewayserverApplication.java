package com.microservice.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator bankingRouterConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/banking/accounts/**")
                        .filters(f -> f
                                .rewritePath("/banking/accounts/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(c -> c.setName("accountsCircuitBreaker")
                                        .setFallbackUri("forward:/contact-support")))
                        .uri("lb://ACCOUNTS"))
                .route(r -> r.path("/banking/cards/**")
                        .filters(f -> f
                                .rewritePath("/banking/cards/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://CARDS"))
                .route(r -> r.path("/banking/loans/**")
                        .filters(f -> f
                                .rewritePath("/banking/loans/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://LOANS"))
                .build();

    }

}
