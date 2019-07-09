package com.zesium.application.beans;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.zesium.application.validation.RegistrationValidationGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank(groups = RegistrationValidationGroup.class)
	@Column(name = "company_name")
	private String companyName;
	
	@NotBlank(groups = RegistrationValidationGroup.class)
	private String organization_number;
	
	@NotBlank(groups = RegistrationValidationGroup.class)
	private String invoice_address;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "company_id")
	@Cascade({CascadeType.ALL})
	private Set<User> users = new HashSet<>();
	
}
