package com.aman.order_service.service;

import com.aman.order_service.config.WebClientConfig;
import com.aman.order_service.dto.InventoryResponse;
import com.aman.order_service.dto.OrderLineItemsDto;
import com.aman.order_service.dto.OrderRequest;
import com.aman.order_service.model.Order;
import com.aman.order_service.model.OrderLineItems;
import com.aman.order_service.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;


    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest
                .getOrderLineItemsDto()
                .stream()
                .map(this::mapToDto).
                toList();

        order.setOrderLineItemsList(orderLineItems);
       List<String> skuCodes= order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //call inventory service and place order if product is available in stock
        InventoryResponse[] inventoryResponseArray = webClient
                .get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse[].class).blockFirst();

        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::getIsInStock);
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock , pleas try again later");
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
