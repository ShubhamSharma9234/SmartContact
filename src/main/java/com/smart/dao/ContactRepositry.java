package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;
import com.smart.entity.User;

public interface ContactRepositry extends JpaRepository<Contact, Integer>{
	
	//pagination

	@Query("Select c from Contact c where c.user.id = :userId ")
	public List<Contact> findContactByUser(@Param("userId") int UserId);
	
	/* search functionality of show contact */
	
	public List<Contact> findByFirstNameContainingAndUser(String firstName,User user);
}
