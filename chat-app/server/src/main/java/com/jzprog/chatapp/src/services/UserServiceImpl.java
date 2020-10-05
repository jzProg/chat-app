package com.jzprog.chatapp.src.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzprog.chatapp.src.advices.LogMethodInfo;
import com.jzprog.chatapp.src.database.UsersRepository;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
    UsersRepository userRepo;
	
	@LogMethodInfo
	@Override
	@Transactional 
	public User searchForUserByUsername(String username) {
		return (User) userRepo.findUserByUsername(username);
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public User searchForUserByUsernameAndPassword(String username, String password) {
		return (User) userRepo.findUserByUsernameAndPassword(username, password);
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public void createNewUser(UserInfo userInfo, String password) {
		User user = new User(userInfo.getUsername(), password, userInfo.getEmail());
        userRepo.save(user);
	}
	
	@LogMethodInfo
	@Override
	@Transactional 
	public User searchForUserByUserId(Integer userId) {
		return (User) userRepo.findUserById(userId);
	}

	@LogMethodInfo
	@Override
	public List<User> searchForUsersMatchingString(String str) {
		return (List<User>) userRepo.findUsersMachingString(str);
	}

	@LogMethodInfo
	@Override
	@Transactional 
	public User updateProfileImage(String username, byte[] newImagePath) {
		User user = searchForUserByUsername(username);
		if (user != null) {
			user.setImage(newImagePath);
			userRepo.save(user);
		}
		return user;
	}

}
