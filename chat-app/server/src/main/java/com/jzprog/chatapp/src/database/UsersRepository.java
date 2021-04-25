package com.jzprog.chatapp.src.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.jzprog.chatapp.src.model.User;

@Repository
@Transactional
public interface UsersRepository extends CrudRepository<User,Integer> {

  User findUserByUsernameAndPassword(String username, String password);
  User findUserByUsername(String username);
  User findUserById(Integer id);
  List<User> findByUsernameIgnoreCaseStartingWith(@Param("str") String str);
}
