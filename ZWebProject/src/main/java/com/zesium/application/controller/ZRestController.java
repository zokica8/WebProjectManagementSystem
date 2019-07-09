package com.zesium.application.controller;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zesium.application.beans.Company;
import com.zesium.application.beans.Login;
import com.zesium.application.beans.User;
import com.zesium.application.beans.UserDetails;
import com.zesium.application.service.CompanyService;
import com.zesium.application.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/restController")
@Slf4j
public class ZRestController {
	
	// attributes
	private UserService userService;
	private CompanyService companyService;
	
	// constructor injection
	@Autowired
	public ZRestController(UserService userService, CompanyService companyService) {
		this.userService = userService;
		this.companyService = companyService;
	}
	
	// services for user
	
	// addUser method, that is needed, so I can use it for android and retrofit
	@PostMapping("/addUserAndroid")
	public ResponseEntity<?> addUserAndroid(@RequestBody UserDetails userDetails) {
		
		List<Company> companies = companyService.findAllCompanies();
		String userEmail = userDetails.getUser().getEmail();
		List<User> userEmails = userService.findUserByEmail(userEmail);
		
		// with HTTP body
		if(userEmails.stream().anyMatch(user -> user.getEmail().equals(userEmail))) {
			log.error("Email already exists!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email already exists!");
		}
		
		String userPhoneNumber = userDetails.getUser().getPhoneNumber();
		List<User> userPhoneNumbers = userService.findUserByPhoneNumber(userPhoneNumber);
		
		if(userPhoneNumbers.stream().anyMatch(user -> user.getPhoneNumber().equals(userPhoneNumber))) {
			log.error("Phone Number already exists!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Phone Number already exists!");
		}
		
		String encoded = DigestUtils.md5Hex(userDetails.getUser().getPassword());
		userDetails.getUser().setPassword(encoded);
		
		if(companies.size() < 1) {
			companyService.addCompany(userDetails.getCompany());
			userService.addUser(userDetails.getUser(), userDetails.getCompany());
			return ResponseEntity.ok(userDetails);
		}
		
		userService.addUser(userDetails.getUser(), companies.get(0));
		return ResponseEntity.status(HttpStatus.OK).body(userDetails);	
	}
	
	@GetMapping("/allUsers")
	public List<User> findUsers() {
		return userService.findUsers();
	}
	
	@GetMapping("/userByName/{name}")
	public List<User> findUserByName(@PathVariable String name) {
		return userService.findUserByName(name);
	}
	
	@GetMapping("/userByEmail/{email}")
	public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
		List<User> userEmails = userService.findUserByEmail(email);
		
		if(!userEmails.stream().anyMatch(user -> user.getEmail().equals(email))) {
			log.error("Email does not exist!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email does not exist! Please try again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(userEmails);
	}
	
	@GetMapping("/userById/{id}")
	public User findUserById(@PathVariable Long id) {
		return userService.findUserById(id);
	}
	
	@PutMapping("/user/{id}")
	public User updateUser(@RequestBody User user, @PathVariable Long id) {	
		return userService.updateUser(user, id);	
	}
	
	@DeleteMapping("/user/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id);
	}
	
	@PostMapping("/loggedInUserAndroid")
	public ResponseEntity<?> validateUserAndroid(@RequestParam(name = "email") String email, 
			@RequestParam(name = "password") String password) {
		Login login = new Login();
		login.setEmail(email);
		login.setPassword(DigestUtils.md5Hex(password));
		
		List<User> emailUsers = userService.findUserByEmail(email);
		
		if(!emailUsers.stream().anyMatch(user -> user.getEmail().equals(login.getEmail()) && 
				user.getPassword().equals(login.getPassword()))) {
			log.error("Email and password don't match!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Email and password don't match! Please try again!");
		}
		
		userService.validateUser(login);
		return ResponseEntity.ok(login);
	}
	
	// services for a company
	@PostMapping("/addCompany")
	public Company addCompany(@RequestBody Company company) {
		return companyService.addCompany(company);
	}
	
	@GetMapping("/allCompanies")
	public List<Company> findAllCompanies() {
		return companyService.findAllCompanies();
	}
	
	@GetMapping("/companyName/{name}")
	public List<Company> findCompanyByName(@PathVariable String name) {
		return companyService.findByName(name);
	}
	
	@GetMapping("/companyId/{id}")
	public Company findCompanyById(@PathVariable Long id) {
		return companyService.findById(id);
	}
	
	@PutMapping("/company/{id}")
	public Company updateCompany(@RequestBody Company company, @PathVariable Long id) {
		return companyService.updateCompany(company, id);
	}
}
