package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
