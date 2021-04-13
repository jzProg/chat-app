package com.jzprog.chatapp.src.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jzprog.chatapp.src.advices.LogMethodInfo;
import com.jzprog.chatapp.src.database.ConversationsRepository;
import com.jzprog.chatapp.src.database.MessagesRepository;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.utils.SystemMessages;

import static java.util.stream.Collectors.toList;

@Service
public class MessagingServiceImpl implements MessagingService {
	
	@Autowired
    UsersRepository userRepo;
	
	@Autowired
	MessagesRepository messagesRepo;
	
	@Autowired
	ConversationsRepository conversationsRepo;
	
	@LogMethodInfo
	@Override
	@Transactional 
	public Set<Conversation> fetchUsersConversations(String username) {
		 User user = (User) userRepo.findUserByUsername(username);
	     return user.getConversations();
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public Conversation createNewConversation(Integer userId, String title, Date date, List<String> members) {
		Conversation newConversation = new Conversation(title.isEmpty() ? SystemMessages.DEFAULT_CONVERSATION_NAME : title, date);      
        User currentUser = (User) userRepo.findUserById(userId);
        for (String memberName : members) {
            User member = (User) userRepo.findUserByUsername(memberName);
            newConversation.getUsers().add(member);
            member.getConversations().add(newConversation); 
        }
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
	
	@LogMethodInfo
	@Override
	@Transactional 
	public void addNewMessageToConversation(Integer convId, String text, Date date, Integer author) {
		Message newMessage = new Message(text, author, date);
		Conversation existingConversation = conversationsRepo.findById(convId);  
		newMessage.setConversation(existingConversation);
		messagesRepo.save(newMessage); 
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public void deleteConversation(Integer convId) {
		Conversation existingConversation = conversationsRepo.findById(convId);
        conversationsRepo.delete(existingConversation);
        for (User user : existingConversation.getUsers()) {
            user.getConversations().remove(existingConversation);
       }
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public List<Message> fetchConversationMessages(Integer convId, int index, int limit) {
		Conversation existingConversation = conversationsRepo.findById(convId);  
		if (existingConversation != null) {
			return existingConversation.getMessages().stream()
					.sorted((m1, m2) -> m2.getCreatedDate().compareTo(m1.getCreatedDate()))
					.skip(index) // skips first n
					.limit(limit)
					.collect(toList());
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> fetchConversationMembers(Integer convId) {
		Conversation existingConversation = conversationsRepo.findById(convId);
		if (existingConversation != null) {
			return existingConversation.getUsers().stream().map(User::getUsername).collect(toList());
		}
		return new ArrayList<>();
	}

}
