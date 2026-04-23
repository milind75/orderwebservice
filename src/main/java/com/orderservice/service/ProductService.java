package com.orderservice.service;

import com.orderservice.entity.Product;
import com.orderservice.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Data

public class ProductService {
    private final ProductRepo productRepo;
    ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
    public Product addProduct(Product product) {
        return productRepo.saveAndFlush(product);
    }
    public Product updateProduct(Product product) {
        return productRepo.saveAndFlush(product);
    }
    public void deleteProduct(Product product) {
        productRepo.delete(product);
    }
    public Product getProductById(String id) {
        Product prod = Optional.of(productRepo.findById(id)).isEmpty() ? null : productRepo.findById(id).get();
        return prod;
    }

    public List<Product> addAll(List<Product> products) {
        return productRepo.saveAll(products);
    }
}
