package com.orderservice.config;
import com.orderservice.entity.Product;
import com.orderservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductService productService;

    public DataInitializer(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only inserts if products don't already exist
        try {
            productService.addAll(java.util.List.of(
                    new Product(generateProductId(), "Laptop", "High performance laptop", 999.99),
                    new Product(generateProductId(), "Mouse", "Wireless optical mouse", 29.99),
                    new Product(generateProductId(), "Keyboard", "Mechanical keyboard", 79.99),
                    new Product(generateProductId(), "Monitor", "27 inch 4K monitor", 399.99),
                    new Product(generateProductId(), "Headphones", "Noise cancelling headset", 149.99)
            ));
        } catch (Exception e) {
            log.error("Error seeding products: {}", e.getMessage());
        }
           log.info("✅ Products seeded successfully!");
    }
    private String generateProductId() {
        int randomNum = new Random().nextInt(9999) + 1;  // 1 to 9999
        String strVal = String.valueOf(randomNum);
        return "PROD" + String.format("%04d", randomNum);
    }
}


