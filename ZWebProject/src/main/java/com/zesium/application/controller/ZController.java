package com.zesium.application.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zesium.application.beans.Company;
import com.zesium.application.beans.Login;
import com.zesium.application.beans.User;
import com.zesium.application.service.CompanyService;
import com.zesium.application.service.UserService;
import com.zesium.application.validation.ForgotPasswordValidationGroup;
import com.zesium.application.validation.RegistrationValidationGroup;

@Controller
@CrossOrigin
public class ZController {

	private UserService service;

	private CompanyService companyService;
	
	@Resource
	private HttpSession session;

	@Autowired
	public ZController(UserService service, CompanyService companyService) {
		this.service = service;
		this.companyService = companyService;
	}

	@GetMapping("/")
	public String homePage() {
		return "home";
	}

	@GetMapping("/signUp")
	public String signUpPage(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("company", new Company());
		return "signUp";
	}

	@PostMapping("/userRegistered")
	public String userRegistered(@ModelAttribute @Validated(value = RegistrationValidationGroup.class) User user,
			BindingResult resultForUsers,
			@ModelAttribute @Validated(value = RegistrationValidationGroup.class) Company company,
			BindingResult resultForCompanies) {

		if (resultForUsers.hasErrors()) {
			return "signUp";
		}

		if (resultForCompanies.hasErrors()) {
			return "signUp";
		}

		List<User> users = service.findUsers();

		if (users.stream().anyMatch(test -> user.getEmail().equals(test.getEmail()))) {
			resultForUsers.rejectValue("email", "email.duplicatekey", "Email already exists. Please try again!");
			return "signUp";
		}

		String encoded = DigestUtils.md5Hex(user.getPassword());
		user.setPassword(encoded);
		insertUserAndCompany(user, company);

		return "userRegistered";
	}

	private void insertUserAndCompany(User user, Company company) {
		List<Company> companies = companyService.findAllCompanies();

		if (companies.size() < 1) {
			companyService.addCompany(company);
			service.addUser(user, company);
		}
		if (companies.size() > 0) {
			service.addUser(user, companies.get(0));
		}
	}

	@GetMapping("/login")
	public ModelAndView loginPage() {
		ModelAndView modelAndView = new ModelAndView("login");
		modelAndView.addObject("login", new Login());

		return modelAndView;
	}

	@PostMapping("/userLoggedIn")
	public ModelAndView userLoggedIn(@ModelAttribute @Valid Login login) {

		ModelAndView modelAndView = null;

		String encoded = DigestUtils.md5Hex(login.getPassword());
		login.setPassword(encoded);

		User user = service.validateUser(login);
		if (user != null) {
			modelAndView = new ModelAndView("userLoggedIn");
			modelAndView.addObject("user", user.getName());
			session.setAttribute("user", user);
		} else {
			modelAndView = new ModelAndView("login");
			modelAndView.addObject("email", "Incorrect email!");
			modelAndView.addObject("password", "Incorrect password!");
		}

		return modelAndView;
	}

	@GetMapping("/forgotPassword")
	public ModelAndView forgotPasswordPage(User user) {

		ModelAndView modelAndView = new ModelAndView("forgotPassword");

		modelAndView.addObject("user", user);

		return modelAndView;

	}

	@PostMapping("/passwordRecovered")
	public ModelAndView passwordRecovored(
			@ModelAttribute @Validated(value = ForgotPasswordValidationGroup.class) User user, BindingResult result) {

		ModelAndView modelAndView = null;
		
		List<User> userEmails = service.findUserByEmail(user.getEmail());

		if (!userEmails.stream().anyMatch(test -> user.getEmail().equals(test.getEmail()))) {
			modelAndView = new ModelAndView("forgotPassword");
			modelAndView.addObject("email", "Email does not exist! Please try again!");
		}
		
		else if(result.hasErrors()) {
			return new ModelAndView("forgotPassword");
		}

		else {
			modelAndView = new ModelAndView("passwordRecovered");
			User userFromStream = userEmails.stream().findAny().get();
			service.updateUser(user, userFromStream.getId());
		}
		return modelAndView;
	}
	
	@PostMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/accessDenied")
	public String accessDenied() {
		return "accessDenied";
	}
	
}