package com.bilircode.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepsitory extends JpaRepository<Customer, Integer> {
}
