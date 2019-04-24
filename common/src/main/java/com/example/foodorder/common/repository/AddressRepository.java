package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
