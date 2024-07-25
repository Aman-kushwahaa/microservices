package com.aman.product_service;

import com.aman.product_service.dto.ProductRequest;
import com.aman.product_service.model.Product;
import com.aman.product_service.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.math.BigDecimal;


import static net.bytebuddy.matcher.ElementMatchers.is;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	//the static keyword is used to declare the
	// mongoDBContainer variable as a static field.
	// This means that the mongoDBContainer instance is
	// shared across all instances of the ProductServiceApplicationTests class.
	//Singleton instance: By making the mongoDBContainer static,
	// you ensure that only one instance of the container is created, and it's shared across all test methods.

	//starting mongodb container dynamically
	@Container
	static MongoDBContainer mongoDBContainer= new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc ;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl); //fetching and configuring mongodb url
	}

	@Test
	void shouldCreateProduct()throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestResponse = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/product").
						contentType(MediaType.APPLICATION_JSON)
						.content(productRequestResponse))
				.andExpect(MockMvcResultMatchers.status().isCreated());
		Assertions.assertEquals(4,productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProduct() throws Exception {
		//create and inject products in db
		for (int i = 1; i <= 3; i++) {
			Product product = Product.builder()
					.name("Product" + i)
					.description("This is product" + i)
					.price(BigDecimal.valueOf(10.99 + (i - 1)))
					.build();
			productRepository.save(product);
		}


			mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/product"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect( MockMvcResultMatchers.jsonPath("$.size()").value(3))
					.andExpect( MockMvcResultMatchers.jsonPath("$[0].name").value("Product1"))
					.andExpect( MockMvcResultMatchers.jsonPath("$[1].name").value("Product2"))
					.andExpect( MockMvcResultMatchers.jsonPath("$[2].name").value("Product3"));



			//setting

		}


	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("TestProduct")
				.description("test Description")
				.price(BigDecimal.valueOf(1255))
				.build();
	}




	@Test
	void contextLoads() {

}
}
