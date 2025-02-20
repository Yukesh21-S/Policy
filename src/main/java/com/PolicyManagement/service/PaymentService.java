package com.PolicyManagement.service;

import com.PolicyManagement.model.Payment;
import com.PolicyManagement.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Method to save the payment
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);  // Saves the payment record to the database
    }

    // You can add more methods to handle other payment-related logic if needed
}
