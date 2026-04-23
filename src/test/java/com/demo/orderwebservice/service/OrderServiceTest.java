package com.demo.orderwebservice.service;

import com.demo.orderwebservice.exception.OrderNotFoundException;
import com.demo.orderwebservice.model.Order;
import com.demo.orderwebservice.model.OrderStatus;
import com.demo.orderwebservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order("Alice", "Laptop", 1, new BigDecimal("999.99"));
        sampleOrder.setId(1L);
    }

    @Test
    void getAllOrders_returnsAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));

        List<Order> orders = orderService.getAllOrders();

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getCustomerName()).isEqualTo("Alice");
    }

    @Test
    void getOrderById_existingId_returnsOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        Order found = orderService.getOrderById(1L);

        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getProductName()).isEqualTo("Laptop");
    }

    @Test
    void getOrderById_nonExistingId_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createOrder_savesAndReturnsOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Order created = orderService.createOrder(sampleOrder);

        assertThat(created.getCustomerName()).isEqualTo("Alice");
        verify(orderRepository, times(1)).save(sampleOrder);
    }

    @Test
    void updateOrder_updatesFields() {
        Order updated = new Order("Bob", "Phone", 2, new BigDecimal("499.99"));
        updated.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateOrder(1L, updated);

        assertThat(result.getCustomerName()).isEqualTo("Bob");
        assertThat(result.getProductName()).isEqualTo("Phone");
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void updateOrderStatus_updatesStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void deleteOrder_deletesExistingOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        doNothing().when(orderRepository).delete(sampleOrder);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).delete(sampleOrder);
    }

    @Test
    void getOrdersByCustomer_returnsMatchingOrders() {
        when(orderRepository.findByCustomerName("Alice")).thenReturn(List.of(sampleOrder));

        List<Order> orders = orderService.getOrdersByCustomer("Alice");

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getCustomerName()).isEqualTo("Alice");
    }

    @Test
    void getOrdersByStatus_returnsMatchingOrders() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(sampleOrder));

        List<Order> orders = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
