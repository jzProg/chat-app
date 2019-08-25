package com.jzprog.chatapp.src.services;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
   @Autowired
   UsersRepository userRepo;
	
   public UserDetails loadUserByUsername(String username) {
	 User dbUser =  (User) userRepo.findUserByUsername(username);
	 return new org.springframework.security.core.userdetails.User(username, dbUser.getPassword(), new ArrayList<>());
   }

}
