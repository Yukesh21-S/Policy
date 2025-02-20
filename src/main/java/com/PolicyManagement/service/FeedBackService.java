package com.PolicyManagement.service;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Feedback;
import com.PolicyManagement.repository.CustomerRepository;
import com.PolicyManagement.repository.FeedBackRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedBackService {


    @Autowired
    private FeedBackRepository feedbackRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Feedback> getFeedbackByPolicyId(Long policyId) {
        return feedbackRepository.findByPolicyId(policyId);
    }

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    public Customers getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Feedback> findAllByPolicyId(Long policyId) {
        return feedbackRepository.findAllByPolicyPolicyId(policyId);
    }
}
