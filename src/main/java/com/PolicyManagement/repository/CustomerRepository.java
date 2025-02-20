package com.PolicyManagement.repository;

import com.PolicyManagement.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customers,Long> {

    Customers findByEmail(String email);

    @Query("SELECT c FROM Customers c JOIN c.policies p WHERE p.policyId = :policyId")
    List<Customers> findCustomersByPolicyId(Long policyId);
    @Query("SELECT c FROM Customers c JOIN c.policies p WHERE p.policyId = :policyId")
    List<Customers> findCustomersByClaimedPolicyId(Long policyId);
}
