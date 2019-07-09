package com.zesium.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;

@Configuration
@ComponentScan(basePackages = "com.zesium.application")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDenied();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new CustomAuthenticationProvider();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and().
			authorizeRequests()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.antMatchers("/**").permitAll()
					.antMatchers("/static/**", "/webjars/**", "/assets/**").permitAll()
						.anyRequest().authenticated()
					.and()
		.formLogin()
			.loginPage("/login")
				.defaultSuccessUrl("/userLoggedIn")
					.failureUrl("/login")
					.permitAll().and()
		.logout()
				.logoutUrl("/logout")
					.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
							.deleteCookies("JSESSIONID").permitAll()
							.and().httpBasic()
							.and().sessionManagement().maximumSessions(1).and()
								.sessionCreationPolicy(SessionCreationPolicy.NEVER)
		.and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
		.and().addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
		
		http.requestMatcher(new AntPathRequestMatcher("/restController/**")).csrf().disable();
	}
}
