package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
}
