package com.aman.order_service.service;

import brave.Span;
import brave.Tracer;
import com.aman.order_service.config.WebClientConfig;
import com.aman.order_service.dto.InventoryResponse;
import com.aman.order_service.dto.OrderLineItemsDto;
import com.aman.order_service.dto.OrderRequest;
import com.aman.order_service.events.OrderPlacedEvent;
import com.aman.order_service.model.Order;
import com.aman.order_service.model.OrderLineItems;
import com.aman.order_service.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(OrderService.class);


    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).
                toList();

        order.setOrderLineItemsList(orderLineItems);
       List<String> skuCodes= order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

       Span inventoryServiceLookup =tracer.nextSpan().name("InventoryServiceLookup").start();
       try(Tracer.SpanInScope spanInScope= tracer.withSpanInScope(inventoryServiceLookup)) {


           //call inventory service and place order if product is available in stock
           InventoryResponse[] inventoryResponseArray = webClientBuilder.build()
                   .get()
                   .uri("http://inventory-service/api/inventory",
                           uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                   .retrieve()
                   .bodyToMono(InventoryResponse[].class).block();


           boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                   .allMatch(InventoryResponse::getIsInStock);
           if (allProductsInStock) {
               orderRepository.save(order);
               kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
               return "Order placed successfully";

           } else {
               throw new IllegalArgumentException("Product is not in stock , pleas try again later");
           }
       }
       finally {
           inventoryServiceLookup.finish();
       }




    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        return orderLineItems;

    }
}
