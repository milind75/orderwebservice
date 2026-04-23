package com.demo.orderwebservice.repository;

import com.demo.orderwebservice.model.Order;
import com.demo.orderwebservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerName(String customerName);

    List<Order> findByStatus(OrderStatus status);
}
