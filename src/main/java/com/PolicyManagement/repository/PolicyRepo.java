package com.PolicyManagement.repository;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepo extends JpaRepository<Policy, Long> {
    List<Policy> findByPolicyStatus(PolicyStatus policyStatus);
    @Query("SELECT p FROM Policy p JOIN p.customers c WHERE c.customerId = :customerId")
    List<Policy> findByCustomers(@Param("customerId") Long customerId);




    @Query("SELECT p FROM Policy p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Policy> findByNameOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

}
