package com.orderservice.repo;

import com.orderservice.entity.OrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMasterRepo extends JpaRepository<OrderMaster, String> {


}
