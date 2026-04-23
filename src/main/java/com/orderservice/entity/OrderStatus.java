package com.orderservice.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderStatus implements Serializable {
    private String orderId;
    private String orderStatus;
    private LocalDateTime orderStatusDate;
}
