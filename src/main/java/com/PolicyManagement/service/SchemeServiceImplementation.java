package com.PolicyManagement.service;

import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.repository.SchemeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchemeServiceImplementation implements SchemeService {

    @Autowired
    private SchemeRepo schemeRepo;
@Autowired
private AdminRepo adminRepo;

    @Override
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can access this method
    public Scheme createScheme(Scheme scheme) {

        Scheme scheme1 = new Scheme(
                scheme.getName(),
                scheme.getDescription(),
                scheme.getEligibility(),
                scheme.getBenefits(),
                "ACTIVE",
                scheme.getTermsAndConditions(),
                scheme.getAdmin()
        );
        return schemeRepo.save(scheme1);
    }


    @PreAuthorize("hasRole('ADMIN')")  // Only admins can access this method
    public Scheme deactivateScheme(Long schemeId) {
        Optional<Scheme> existingScheme = schemeRepo.findById(schemeId);

        if (existingScheme.isPresent()) {
            Scheme scheme = existingScheme.get();
            scheme.setStatus("INACTIVE");  // Mark scheme as inactive
            return schemeRepo.save(scheme);
        }

        return null;
    }
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can access this method
    public Scheme activateScheme(Long schemeId) {
        Optional<Scheme> existingScheme = schemeRepo.findById(schemeId);

        if (existingScheme.isPresent()) {
            Scheme scheme = existingScheme.get();
            scheme.setStatus("ACTIVE");  // Mark scheme as inactive
            return schemeRepo.save(scheme);
        }

        return null;
    }
    public List<Scheme> getAllSchemes()
    {
        return schemeRepo.findAll();
    }

    public Scheme getSchemeById(Long id) {
        return schemeRepo.findById(id).get();
    }

    public List<Scheme> getAllActiveSchemes() {
        return schemeRepo.findByStatus("ACTIVE");
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePolicy(Long id) {
        schemeRepo.deleteById(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Scheme updateScheme(Scheme scheme) {

        Scheme scheme1 =schemeRepo.findById(scheme.getSchemeId()).get();
        scheme1.setName(scheme.getName());
        scheme1.setEligibility(scheme.getEligibility());
        scheme1.setDescription(scheme.getDescription());
        scheme1.setBenefits(scheme.getBenefits());
        scheme1.setStatus("ACTIVE");
        scheme1.setTermsAndConditions(scheme.getTermsAndConditions());
        scheme1.setAdmin(scheme.getAdmin());
        schemeRepo.save(scheme1);

        return scheme1;
    }
}
