
package com.jzprog.chatapp.src.database;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.jzprog.chatapp.src.model.Message;

@Repository
public interface MessagesRepository extends CrudRepository<Message,Integer> {

  Message findById(Integer id);
  List<Message> findByPostedBy(Integer posted_by);
  List<Message> findByConversationId(Integer conversation_id);
}
