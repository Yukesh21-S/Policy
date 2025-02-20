package com.PolicyManagement.service;


import com.PolicyManagement.model.Customers;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface CustomerService extends UserDetailsService {
    Customers save(Customers registrationDto);
    Customers update(long customerId,Customers registrationDto);
    String delete(long customerId);
    Customers findByCustomerId(long customerId);

    Optional<Customers> findByEmail(String adminEmail);

    Long getCustomerIdByUsername(String username);

    boolean verifyPassword(String username, String password);

    List<Customers> getCustomersByPolicyId(Long id);

    void updateCustomer(Customers customer);
}