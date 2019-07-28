
package com.jzprog.chatapp.src.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.jzprog.chatapp.src.model.Message;

@Repository
public interface MessagesRepository extends CrudRepository<Message,Integer> {

  Message findById(Integer id);
}
