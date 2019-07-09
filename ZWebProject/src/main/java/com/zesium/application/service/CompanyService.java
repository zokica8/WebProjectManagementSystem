package com.zesium.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.zesium.application.beans.Company;
import com.zesium.application.repository.CompanyRepository;

@Service
public class CompanyService {
	
	// fields
	private CompanyRepository companyRepository;

	// constructor injection
	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}
	
	// adding the company in the database
	public Company addCompany(Company company) {
		return companyRepository.saveAndFlush(company);
	}
	
	// all companies in the database (should be 1 only)
	public List<Company> findAllCompanies() {
		return companyRepository.findAll();
	}
	
	// find company by name
	public List<Company> findByName(String companyName) {
		return companyRepository.findByCompanyName(companyName);
	}
	
	// find company by id
	public Company findById(Long id) {
		Company company = companyRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Company with id " + id + " doesn't exist!"));
		
		return company;
	}
	
	// updating the company
	public Company updateCompany(Company company, Long id) {
		// first you need to see if company exists!
		Optional<Company> companyExists = companyRepository.findById(id);
		
		if(companyExists.isPresent()) {
			Company updated = companyExists.get();
			
			updated.setCompanyName(company.getCompanyName());
			updated.setOrganization_number(company.getOrganization_number());
			updated.setInvoice_address(company.getInvoice_address());
			updated.setUsers(company.getUsers());
			
			return companyRepository.save(updated);
		}
		
		else {
			throw new IllegalArgumentException("Company can't be updated with id: " + id);
		}
	}

}
