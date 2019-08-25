package com.jzprog.chatapp.src.services;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.User;

@Service
public class MessagingService {
	
	@Autowired
    UsersRepository userRepo;
	
	public Set<Conversation> fetchUsersConversations(String username) {
		 User user = (User) userRepo.findUserByUsername(username);
	     return user.getConversations();
	}
	
	public Conversation createNewConversation(Integer userId, String title, Date date) {
		Conversation newConversation = new Conversation(title, date);      
        User currentUser = (User) userRepo.findUserById(userId);
        newConversation.getUsers().add(currentUser);
        currentUser.getConversations().add(newConversation);    
        userRepo.save(currentUser); 
        return newConversation;
	}

}
