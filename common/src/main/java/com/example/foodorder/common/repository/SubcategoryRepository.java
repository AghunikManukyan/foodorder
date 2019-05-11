package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Integer> {
    Subcategory findAllByName(String name);
}
