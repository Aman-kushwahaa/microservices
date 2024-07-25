package com.aman.inventory_service;

import com.aman.inventory_service.model.Inventory;
import com.aman.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {//commandlinerunner interface is use when we want run some commands at the start of the application

		return args -> {
			Inventory inventory1 = new Inventory();
			inventory1.setQuantity(100);
			inventory1.setSkuCode("iphone 13");

			Inventory inventory2 = new Inventory();
			inventory2.setQuantity(0);
			inventory2.setSkuCode("iphone 14");

			inventoryRepository.save(inventory1);
			inventoryRepository.save(inventory2);
		};
	}
}
