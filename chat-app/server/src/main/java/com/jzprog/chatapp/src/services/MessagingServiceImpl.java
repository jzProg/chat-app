package com.jzprog.chatapp.src.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	private static final int CONVERSATION_LIMIT = 3;
	
	@LogMethodInfo
	@Override
	@Transactional 
	public Set<Conversation> fetchUsersConversations(String username, int index) {
		 User user = userRepo.findUserByUsername(username);
	     return user.getConversations().stream()
				 .sorted((m1, m2) -> m2.getCreatedDate().compareTo(m1.getCreatedDate()))
				 .skip((long) index * CONVERSATION_LIMIT) // skips first n
				 .limit(CONVERSATION_LIMIT)
				 .collect(Collectors.toSet());
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public Conversation createNewConversation(Integer userId, String title, Date date, List<String> members) {
		Conversation newConversation = new Conversation(title.isEmpty() ? SystemMessages.DEFAULT_CONVERSATION_NAME : title, date);
		newConversation.setOwnerId(userId);
        User currentUser = userRepo.findUserById(userId);
        for (String memberName : members) {
            User member = userRepo.findUserByUsername(memberName);
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
	public Message addNewMessageToConversation(Integer convId, String text, Date date, Integer author) {
		Message newMessage = new Message(text, author, date);
		Conversation existingConversation = getExistingConversation(convId);
		newMessage.setConversation(existingConversation);
		messagesRepo.save(newMessage);
		return newMessage;
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public void deleteConversation(Integer convId) {
		Conversation existingConversation = getExistingConversation(convId);
        conversationsRepo.delete(existingConversation);
        for (User user : existingConversation.getUsers()) {
            user.getConversations().remove(existingConversation);
       }
	}

	@LogMethodInfo
	@Override
	@Transactional
	public Conversation removeConversationMember(Integer id, User user) {
		Conversation existingConversation = getExistingConversation(id);
		if (existingConversation != null) {
			existingConversation.getUsers().removeIf(u -> u.getId().equals(user.getId()));
			user.getConversations().removeIf(c -> c.getId().equals(id));
			userRepo.save(user);
		}
		return existingConversation;
	}

	@LogMethodInfo
	@Override
	@Transactional 
	public List<Message> fetchConversationMessages(Integer convId, int index, int limit) {
		Conversation existingConversation = getExistingConversation(convId);
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
		Conversation existingConversation = getExistingConversation(convId);
		if (existingConversation != null) {
			return existingConversation.getUsers().stream().map(User::getUsername).collect(toList());
		}
		return new ArrayList<>();
	}

	@Override
	public Conversation getExistingConversation(Integer convId) {
		return conversationsRepo.findById(convId);
	}

	@LogMethodInfo
	@Override
	@Transactional
	public Conversation addMemberToConversation(Integer convId, List<String> members) {
		Conversation conversation = conversationsRepo.findById(convId);
		if (conversation != null) {
			conversation.getMessages().size();
			for (String memberName : members) {
				User member = userRepo.findUserByUsername(memberName);
				if (member != null) {
					conversation.getUsers().add(member);
					member.getConversations().add(conversation);
				}
			}
			conversationsRepo.save(conversation);
		}
		return conversation;
	}
}
