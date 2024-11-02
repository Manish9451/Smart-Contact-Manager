package com.smartContactManager.SmartContactManager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartContactManager.SmartContactManager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
}
