package com.jzprog.chatapp.src.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
    UsersRepository userRepo;
	
	@Override
	@Transactional 
	public User searchForUserByUsername(String username) {
		return (User) userRepo.findUserByUsername(username);
	}
	
	@Override
	@Transactional 
	public User searchForUserByUsernameAndPassword(String username, String password) {
		return (User) userRepo.findUserByUsernameAndPassword(username, password);
	}
	
	@Override
	@Transactional 
	public void createNewUser(UserInfo userInfo, String password) {
		User user = new User(userInfo.getUsername(), password, userInfo.getEmail());
        userRepo.save(user);
	}
	
	@Override
	@Transactional 
	public User searchForUserByUserId(Integer userId) {
		return (User) userRepo.findUserById(userId);
	}

}
