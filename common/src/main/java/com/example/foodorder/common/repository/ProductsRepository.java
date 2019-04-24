package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {

    List<Products> findAllByUserId(int id);

}



    
