package com.PolicyManagement.service;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Role;
import com.PolicyManagement.model.Admin;
import com.PolicyManagement.password.PasswordValidator;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.PolicyManagement.model.Role.ADMIN;
import static com.PolicyManagement.model.Role.CUSTOMER;

@Slf4j
@Service
public class CustomerServiceImplementation implements CustomerService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final AdminRepo adminRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImplementation.class);

    @Autowired
    public CustomerServiceImplementation(
            BCryptPasswordEncoder passwordEncoder,
            CustomerRepository customerRepository,
            AdminRepo adminRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public Customers save(Customers registrationDto) {
        if (!PasswordValidator.validatePassword(registrationDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the required criteria.");
        }

        Customers customer = new Customers(
                registrationDto.getName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                "ACTIVE",
                registrationDto.getDateOfRegistration(),
                registrationDto.getAddress(),
                CUSTOMER
        );
        return customerRepository.save(customer);
    }

    public Customers update(long customerId, Customers registrationDto) {
        Optional<Customers> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer with ID " + customerId + " not found.");
        }

        Customers customer = optionalCustomer.get();
        customer.setName(registrationDto.getName());
        customer.setEmail(registrationDto.getEmail());
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        customer.setDateOfRegistration(registrationDto.getDateOfRegistration());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setStatus(registrationDto.getStatus());
        customer.setRole(CUSTOMER);

        if (registrationDto.getAddress() != null) {
            if (customer.getAddress() == null) {
                customer.setAddress(registrationDto.getAddress());
            } else {
                customer.getAddress().setStreet(registrationDto.getAddress().getStreet());
                customer.getAddress().setCity(registrationDto.getAddress().getCity());
                customer.getAddress().setState(registrationDto.getAddress().getState());
                customer.getAddress().setCountry(registrationDto.getAddress().getCountry());
            }
        }

        return customerRepository.save(customer);
    }

    public Customers findByCustomerId(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        return customer.orElse(null);
    }

    @Override
    public Optional<Customers> findByEmail(String username) {
        return Optional.ofNullable(customerRepository.findByEmail(username));
    }

    @Override
    public Long getCustomerIdByUsername(String username) {
        Optional<Customers> customerOptional = Optional.ofNullable(customerRepository.findByEmail(username));
        return customerOptional.map(Customers::getCustomerId).orElse(null); // Assuming `getId()` returns the `customerId`
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        Optional<Customers> customerOptional = Optional.ofNullable(customerRepository.findByEmail(email));
        if (customerOptional.isPresent()) {
            Customers customer = customerOptional.get();
            // Assuming password encryption is used, compare encrypted passwords
            return passwordEncoder.matches(password, customer.getPassword());
        }
        return false;
    }

    @Override
    public List<Customers> getCustomersByPolicyId(Long policyId) {
        return customerRepository.findCustomersByPolicyId(policyId);
    }

    @Override
    public void updateCustomer(Customers customer) {
        // Ensure the customer exists in the database
        Optional<Customers> existingCustomerOpt = customerRepository.findById(customer.getCustomerId());
        if (existingCustomerOpt.isPresent()) {
            Customers existingCustomer = existingCustomerOpt.get();

            // Update relevant fields
            existingCustomer.setName(customer.getName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setPassword(customer.getPassword()); // Already encoded

            // Save the updated customer
            customerRepository.save(existingCustomer);
        } else {
            throw new IllegalArgumentException("Customer not found with ID: " + customer.getCustomerId());
        }
    }

    public List<Customers> getCustomersByClaimedPolicyId(Long policyId) {
        return customerRepository.findCustomersByClaimedPolicyId(policyId);
    }

    public String delete(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            Customers customerToDelete = customer.get();
            customerToDelete.setStatus("DEACTIVATED");
            customerRepository.save(customerToDelete);
            return "Successfully DEACTIVATED";
        }
        return "Customer not found";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, check if the username belongs to a customer
        Optional<Customers> customerOptional = Optional.ofNullable(customerRepository.findByEmail(username));
        if (customerOptional.isPresent()) {
            Customers customer = customerOptional.get();
            return User.builder()
                    .username(customer.getEmail())
                    .password(customer.getPassword())
                    .roles(customer.getRole().toString()) // Role as a string
                    .build();
        }

        // If not a customer, check if the username belongs to an admin
        Optional<Admin> adminOptional = adminRepository.findByEmail(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .roles(admin.getRole().toString()) // Role as a string
                    .build();
        }

        // If neither, throw an exception
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}