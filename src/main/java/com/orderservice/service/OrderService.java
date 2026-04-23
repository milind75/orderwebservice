package com.orderservice.service;

import com.orderservice.entity.OrderMaster;
import com.orderservice.repo.OrderMasterRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class OrderService {
    @Autowired
    private OrderMasterRepo orderMasterRepo;
    public OrderMaster createOrder( OrderMaster orderMaster) {
        return orderMasterRepo.save(orderMaster);
    }

     public OrderMaster getOrderById(String orderId) {
        return orderMasterRepo.findById(orderId).orElse(null);
    }
}
