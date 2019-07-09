package com.zesium.application.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.zesium.application.validation.ForgotPasswordValidationGroup;
import com.zesium.application.validation.RegistrationValidationGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="name_and_surname")
	@NotBlank(groups = RegistrationValidationGroup.class)
	@Size(min = 5, max = 50, groups = RegistrationValidationGroup.class)
	private String name;
	
	@NotBlank(groups = {RegistrationValidationGroup.class, ForgotPasswordValidationGroup.class})
	@Pattern(regexp = "^(.+)@(.+)$", message = "Not a valid email address!",
			groups = {RegistrationValidationGroup.class, ForgotPasswordValidationGroup.class})
	private String email;
	
	@NotBlank(groups = {RegistrationValidationGroup.class, ForgotPasswordValidationGroup.class})
	@Size(min = 8, message = "Password must have at least 8 non space characters!",
			groups = {RegistrationValidationGroup.class, ForgotPasswordValidationGroup.class})
	private String password;
	
	@Column(name="phone_number")
	@NotBlank(groups = RegistrationValidationGroup.class)
	private String phoneNumber;
	
	private Integer company_id;
	
}
