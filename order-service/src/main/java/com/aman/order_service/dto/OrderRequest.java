package com.aman.order_service.dto;


import com.aman.order_service.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
   private List<OrderLineItemsDto> orderLineItemsDtoList;
}
