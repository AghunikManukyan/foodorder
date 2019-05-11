package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Integer> {


    Menu findAllByName(String name);

}
