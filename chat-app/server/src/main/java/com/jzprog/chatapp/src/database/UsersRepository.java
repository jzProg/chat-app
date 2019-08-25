package com.jzprog.chatapp.src.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.jzprog.chatapp.src.model.User;

@Repository
public interface UsersRepository extends CrudRepository<User,Integer> {

  User findUserByUsernameAndPassword(String username, String password);
  User findUserByUsername(String username);
}
