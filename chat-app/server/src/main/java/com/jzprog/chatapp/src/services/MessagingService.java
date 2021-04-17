package com.jzprog.chatapp.src.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.User;

public interface MessagingService {
	Set<Conversation> fetchUsersConversations(String username, int index);
	Conversation createNewConversation(Integer userId, String title, Date date, List<String> members);
	void addNewMessageToConversation(Integer convId, String text, Date date, Integer author);
	List<Message> fetchConversationMessages(Integer convId, int index, int limit);
	List<String> fetchConversationMembers(Integer convId);
	Conversation getExistingConversation(Integer convId);
	void deleteConversation(Integer id);
	Conversation removeConversationMember(Integer id, User user);
	Conversation addMemberToConversation(Integer convId, List<String> members);
}
