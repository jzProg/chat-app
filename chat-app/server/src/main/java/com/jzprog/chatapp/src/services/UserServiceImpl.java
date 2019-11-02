package com.jzprog.chatapp.src.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
    UsersRepository userRepo;
	
	@Override
	public User searchForUserByUsername(String username) {
		return (User) userRepo.findUserByUsername(username);
	}
	
	@Override
	public User searchForUserByUsernameAndPassword(String username, String password) {
		return (User) userRepo.findUserByUsernameAndPassword(username, password);
	}
	
	@Override
	public void createNewUser(UserInfo userInfo, String password) {
		User user = new User(userInfo.getUsername(), password, userInfo.getEmail());
        userRepo.save(user);
	}

}
