package com.aman.inventory_service.service;

import com.aman.inventory_service.dto.InventoryResponse;
import com.aman.inventory_service.model.Inventory;
import com.aman.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    Logger logger = LoggerFactory.getLogger(InventoryService.class);
    @SneakyThrows //not for production
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode)  {
        //reproducing slow response
        logger.info("wait started");
        Thread.sleep(10000);
        logger.info("wait ended");

        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCode);
        logger.info(inventories.toString());



       return  skuCode.stream().map(sku->{
           Inventory inventory  = inventories.stream()
                   .filter(i->i.getSkuCode().equals(sku)).findFirst().
                    orElse(null);
                    boolean isInStock = inventory != null && inventory.getQuantity()>0;
                    return new InventoryResponse(sku, isInStock);
               }).toList();
//               .findBySkuCodeIn(skuCode).stream().map(inventory->
//
//
//                    InventoryResponse.builder()
//                   .skuCode(inventory.getSkuCode())
//
//                   .isInStock(inventory.getQuantity()>0)
//                   .build()
//       ).toList();



    }
}
//add tranactional in ordeerSerive at class level