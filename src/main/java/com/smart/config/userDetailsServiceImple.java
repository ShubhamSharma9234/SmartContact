package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepositry;
import com.smart.entity.User;

public class userDetailsServiceImple implements UserDetailsService{

	@Autowired
	private UserRepositry repositry ;	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user from database
		User user = repositry.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("could not found user name");
		}
		
		CustomUserDetail customUserDetail= new CustomUserDetail(user);
		
		return customUserDetail;
	}

}
