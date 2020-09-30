package com.jzprog.chatapp.src.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jzprog.chatapp.src.model.Conversation;

@Repository
public interface ConversationsRepository extends CrudRepository<Conversation,Integer> {

	Conversation findById(Integer id);

}
