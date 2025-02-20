package com.PolicyManagement.repository;

import com.PolicyManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // You can define custom queries here if needed
}
