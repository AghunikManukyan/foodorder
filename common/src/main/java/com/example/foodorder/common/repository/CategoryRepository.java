package com.example.foodorder.common.repository;

 import com.example.foodorder.common.model.Category;
 import com.example.foodorder.common.model.Menu;
 import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {


 Category findAllByName(String name);
}
