package com.orderservice.controller;

import com.orderservice.entity.OrderMaster;
import com.orderservice.repo.OrderMasterRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Controller", description = "APIs for managing Orders")
public class OrderController {

    private final OrderMasterRepo orderMasterRepo;

    public OrderController(OrderMasterRepo orderMasterRepo) {
        this.orderMasterRepo = orderMasterRepo;
    }

    @Operation(
            summary = "Get an order by ID",
            description = "Retrieves a single OrderMaster record by its Order ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderMaster.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content
            )
    })
    @GetMapping("/GetOrder")
    public ResponseEntity<OrderMaster> getOrderInfo(
            @Parameter(description = "The unique Order ID", required = true, example = "ORD-001")
            @RequestParam String orderId
    ) {
        OrderMaster orderMaster = this.orderMasterRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok().body(orderMaster);
    }

    @Operation(
            summary = "Create a new order",
            description = "Saves a new OrderMaster record along with its order details and status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderMaster.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            )
    })
    @PostMapping(value = "/CreateOrder", produces = "application/json")
    public ResponseEntity<OrderMaster> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "OrderMaster object to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderMaster.class))
            )
            @RequestBody OrderMaster orderMaster
    ) {
        OrderMaster savedOrder = this.orderMasterRepo.save(orderMaster);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }
}
