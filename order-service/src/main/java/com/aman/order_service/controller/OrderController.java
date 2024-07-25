package com.aman.order_service.controller;


import com.aman.order_service.dto.OrderRequest;
import com.aman.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {

    Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("Order received: {}", orderRequest);
        orderService.placeOrder(orderRequest);

        return "Order Placed Successfully";
    }
}
