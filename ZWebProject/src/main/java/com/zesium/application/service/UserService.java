package com.zesium.application.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zesium.application.beans.Company;
import com.zesium.application.beans.Login;
import com.zesium.application.beans.User;
import com.zesium.application.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	// fields
	private UserRepository userRepository;

	private JdbcTemplate jdbcTemplate;

	// constructors
	// injecting the userRepository via constructor injection
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// setter method for injecting JdbcTemplate
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// adding an user in the database
	public User addUser(User user, Company company) {
		String sql = "insert into user "
				+ "(name_and_surname, email, password, phone_number, company_id) "
				+ "values (?, ?, ?, ?, ?);";
		
		jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getPassword(),
				user.getPhoneNumber(), company.getId());
		
		return user;
	}

	// finding all users in the database
	public List<User> findUsers() {
		return userRepository.findAll();
	}

	// finding user by name
	public List<User> findUserByName(String name) {
		return userRepository.findByName(name);
	}
	
	// finding user by email
	public List<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	// this is a method that will try to find the user in the database and authenticate him!
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users = userRepository.findByEmail(username);
		User user = users.stream().findFirst()
				.orElseThrow(() -> new UsernameNotFoundException("User with email: " + username + " not found!"));
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), null);
	}
	
	// finding user by phoneNumber
	public List<User> findUserByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
	}
	
	// finding user by id
	public User findUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User with id " + id + " can't be found!"));

		return user;
	}

	// updating the user
	public User updateUser(User user, Long id) {
		// you need to have a logic to see if the user exists!
		Optional<User> userCheck = userRepository.findById(id);
		if (userCheck.isPresent()) {
			User updated = userCheck.get();

			updated.setEmail(user.getEmail());
			updated.setPassword(DigestUtils.md5Hex(user.getPassword()));
			
			return userRepository.save(updated);
		} else {
			throw new IllegalArgumentException("User can't be updated with id: " + id);
		}
	}

	// deleting the user
	public ResponseEntity<User> deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User with id " + id + " can't be found!"));

		userRepository.delete(user);
		
		// maybe change this!
		return ResponseEntity.ok().build();
	}

	// Spring JDBC method for validating the user email and password
	public User validateUser(Login login) {
		String sql = "select * from user where email ='" + login.getEmail() + "' and password ='" + login.getPassword()
				+ "'";

		List<User> users = jdbcTemplate.query(sql, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setName(rs.getString("name_and_surname"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setPhoneNumber(rs.getString("phone_number"));
				user.setCompany_id(rs.getInt("company_id"));
				return user;
			}

		});		
		return users.size() > 0 ? users.get(0) : null;
	}
}