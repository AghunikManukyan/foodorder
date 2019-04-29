package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Address;
import com.example.foodorder.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Address findAddressByUser(User user);
}
