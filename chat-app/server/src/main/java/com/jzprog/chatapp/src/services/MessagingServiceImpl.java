package com.jzprog.chatapp.src.services;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzprog.chatapp.src.controllers.MessagesController;
import com.jzprog.chatapp.src.database.ConversationsRepository;
import com.jzprog.chatapp.src.database.MessagesRepository;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.utils.SystemMessages;

@Service
public class MessagingServiceImpl implements MessagingService {
	
    Logger log = Logger.getLogger(MessagingServiceImpl.class.getName());

	
	@Autowired
    UsersRepository userRepo;
	
	@Autowired
	MessagesRepository messagesRepo;
	
	@Autowired
	ConversationsRepository conversationsRepo;
	
	@Override
	@Transactional 
	public Set<Conversation> fetchUsersConversations(String username) {
		 User user = (User) userRepo.findUserByUsername(username);
	     return user.getConversations();
	}
	
	@Override
	@Transactional 
	public Conversation createNewConversation(Integer userId, String title, Date date) {
		Conversation newConversation = new Conversation(title.isEmpty() ? SystemMessages.DEFAULT_CONVERSATION_NAME : title, date);      
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
	@Transactional 
	public void addNewMessageToConversation(Integer convId, String text, Date date, Integer author) {
		Message newMessage = new Message(text, author, date);
		Conversation existingConversation = conversationsRepo.findById(convId);  
		newMessage.setConversation(existingConversation);
		messagesRepo.save(newMessage); 
	}
	
	@Override
	@Transactional 
	public void deleteConversation(Integer convId) {
		Conversation existingConversation = conversationsRepo.findById(convId);
        log.info("existing conv: "  + existingConversation);
        conversationsRepo.delete(existingConversation);
        for (User user : existingConversation.getUsers()) {
            user.getConversations().remove(existingConversation);
       }
	}
	
	@Override
	@Transactional 
	public List<Message> fetchConversationMessages(Integer convId) {
		return conversationsRepo.findById(convId).getMessages();
	}

}
