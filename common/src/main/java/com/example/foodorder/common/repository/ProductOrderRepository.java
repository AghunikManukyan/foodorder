package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {


   List<ProductOrder> findAllByUserId(int id);

}
