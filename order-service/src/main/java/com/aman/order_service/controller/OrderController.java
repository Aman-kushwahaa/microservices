package com.aman.order_service.controller;


import com.aman.order_service.dto.OrderRequest;
import com.aman.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {

    Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @CircuitBreaker(name ="inventory",fallbackMethod = "fallbackMethod" )
    @TimeLimiter(name = "inventory")
    @Retry(name="inventory")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompletableFuture<String> createOrder(@RequestBody OrderRequest orderRequest) {

        logger.info("Order received: {}", orderRequest);
        orderService.placeOrder(orderRequest);
       return  CompletableFuture.supplyAsync(()-> orderService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException e) {
        return CompletableFuture.supplyAsync(()->"Oops! Something went wrong. Please order again later");
    }
}
