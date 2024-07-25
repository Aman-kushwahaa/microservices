package com.aman.product_service.service;

import com.aman.product_service.dto.ProductRequest;
import com.aman.product_service.dto.ProductResponse;
import com.aman.product_service.model.Product;
import com.aman.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;


    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved ", product.getId());
    }


    //getALlProducts
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
          return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
       return  ProductResponse.builder()
               .name(product.getName())
               .id(product.getId())
               .description(product.getDescription())
               .price(product.getPrice())
               .build();

    }
}
