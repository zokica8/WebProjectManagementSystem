package com.zesium.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zesium.application.beans.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	 
	List<User> findByName(String name);
	List<User> findByEmail(String email);
	List<User> findByPhoneNumber(String phoneNumber);
}
