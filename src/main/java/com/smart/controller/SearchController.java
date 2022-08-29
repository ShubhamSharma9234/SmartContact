package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepositry;
import com.smart.dao.UserRepositry;
import com.smart.entity.Contact;
import com.smart.entity.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepositry userRepositry;
	
	@Autowired
	private ContactRepositry contactRepositry;
	
	/* search handler */
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query ,Principal principal){
		
		System.out.println(query);
		
		User user = this.userRepositry.getUserByUserName(principal.getName());
		
		List<Contact> contacts = this.contactRepositry.findByFirstNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
}
