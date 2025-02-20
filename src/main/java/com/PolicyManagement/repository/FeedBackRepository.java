package com.PolicyManagement.repository;

import com.PolicyManagement.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FeedBackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE f.policy.policyId = :policyId")
    List<Feedback> findByPolicyId(@Param("policyId") Long policyId);

    // Find Feedback by Customer ID
    @Query("SELECT f FROM Feedback f WHERE f.customer.customerId = :customerId")
    List<Feedback> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT f FROM Feedback f WHERE f.policy.policyId = :policyId")
    List<Feedback> findAllByPolicyPolicyId(@Param("policyId") Long policyId);
}
