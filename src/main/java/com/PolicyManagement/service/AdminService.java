package com.PolicyManagement.service;


import com.PolicyManagement.model.Admin;
import com.PolicyManagement.repository.AdminRepo;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepo adminRepository;

    public AdminService(AdminRepo adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin findAdminByEmail(String email) {
        return adminRepository.findByEmail(email).get(); // Assuming email is unique and used for login
    }
}
