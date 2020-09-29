package com.jzprog.chatapp.src.services;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzprog.chatapp.src.database.MessagesRepository;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.User;

@Service
public class MessagingServiceImpl implements MessagingService {
	
	@Autowired
    UsersRepository userRepo;
	
	@Autowired
	MessagesRepository messagesRepo;
	
	@Override
	public Set<Conversation> fetchUsersConversations(String username) {
		 User user = (User) userRepo.findUserByUsername(username);
	     return user.getConversations();
	}
	
	@Override
	@Transactional 
	public Conversation createNewConversation(Integer userId, String title, Date date) {
		Conversation newConversation = new Conversation(title.equals("") ? "Unamed" : title, date);      
        User currentUser = (User) userRepo.findUserById(userId);
        newConversation.getUsers().add(currentUser);
        currentUser.getConversations().add(newConversation);    
        userRepo.save(currentUser); 
        for (Iterator<Conversation> it = currentUser.getConversations().iterator(); it.hasNext(); ) {
        	Conversation conv = it.next();
            if (conv.getTitle().equals(newConversation.getTitle()) && conv.getCreatedDate().equals(newConversation.getCreatedDate())) {
            	newConversation.setId(conv.getId());
            	break;
            }
            	
        }
        return newConversation;
	}
	
	@Override
	public Message addNewMessageToConversation(Integer convId, String text, Date date, Integer author) {
		Message newMessage = new Message(text, author, date, convId);
		messagesRepo.save(newMessage); 
        return newMessage;
	}
	
	@Override
	public List<Message> fetchConversationMessages(Integer convId) {
		 return messagesRepo.findByConversationId(convId);
	}

}
