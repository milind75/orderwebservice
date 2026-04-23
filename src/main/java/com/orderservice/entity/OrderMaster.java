package com.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderMaster {
    @Id
    String orderId;
    LocalDate orderDate;
    String totalPrice;
    String paymentMethod;

    @OneToMany(mappedBy = "orderMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_status", joinColumns = @JoinColumn(name = "order_id"))
    @AttributeOverride(name = "orderId", column = @Column(name = "status_order_id"))
    private List<OrderStatus> orderStatus;
}
