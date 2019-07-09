package com.zesium.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zesium.application.beans.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	
	List<Company> findByCompanyName(String companyName);

}
