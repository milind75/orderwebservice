package com.orderservice.controller;

import com.orderservice.entity.Product;
import com.orderservice.service.OrderService;
import com.orderservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")               // ✅ Fixed: @RestController doesn't take a path
@Tag(name = "Product Controller", description = "APIs for managing Products")
public class ProductController {

    private final ProductService productService;


    // ✅ Fixed: constructor was injecting ProductController into itself
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Add a single product",
            description = "Creates and saves a single Product record"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            )
    })
    @PostMapping(value = "/add", produces = "application/json")
    public ResponseEntity<Product> addProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product object to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class))
            )
            @RequestBody Product product
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product));
    }

    @Operation(
            summary = "Add multiple products",
            description = "Creates and saves a list of Product records in bulk"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Products created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            )
    })
    @PostMapping(value = "/addAll", produces = "application/json")
    public ResponseEntity<List<Product>> addAllProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of Product objects to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class))
            )
            @RequestBody List<Product> products
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addAll(products));
    }
}
