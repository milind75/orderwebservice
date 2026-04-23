package com.demo.orderwebservice.controller;

import com.demo.orderwebservice.model.Order;
import com.demo.orderwebservice.model.OrderStatus;
import com.demo.orderwebservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order("Alice", "Laptop", 1, new BigDecimal("999.99"));
        sampleOrder.setId(1L);
    }

    @Test
    void getAllOrders_returnsOk() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Alice"))
                .andExpect(jsonPath("$[0].productName").value("Laptop"));
    }

    @Test
    void getOrderById_returnsOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(sampleOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"));
    }

    @Test
    void createOrder_returnsCreated() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Alice"));
    }

    @Test
    void createOrder_withMissingCustomerName_returnsBadRequest() throws Exception {
        Order invalid = new Order(null, "Laptop", 1, new BigDecimal("999.99"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrder_returnsOk() throws Exception {
        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(sampleOrder);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    void updateOrderStatus_returnsOk() throws Exception {
        sampleOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)).thenReturn(sampleOrder);

        mockMvc.perform(patch("/api/orders/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void deleteOrder_returnsNoContent() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getOrdersByCustomer_returnsOrders() throws Exception {
        when(orderService.getOrdersByCustomer("Alice")).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders/customer/Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Alice"));
    }

    @Test
    void getOrdersByStatus_returnsOrders() throws Exception {
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
