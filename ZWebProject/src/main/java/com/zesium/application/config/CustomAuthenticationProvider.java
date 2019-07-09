package com.zesium.application.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.zesium.application.beans.User;
import com.zesium.application.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserService service;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		
		List<User> users = service.findUserByEmail(token.getName());
		
		User user = users.stream().findAny().orElseThrow(() -> 
			new UsernameNotFoundException("User with email: " + token.getName() + "not found!"));
		
		if(!user.getPassword().equals(token.getCredentials().toString())) {
			throw new BadCredentialsException("Invalid credentials!");
		}
		
		return new UsernamePasswordAuthenticationToken(user, user.getPassword());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}

}
