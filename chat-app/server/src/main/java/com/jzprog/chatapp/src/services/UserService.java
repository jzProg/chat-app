package com.jzprog.chatapp.src.services;

import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;

public interface UserService {
	User searchForUserByUsername(String username);
	User searchForUserByUsernameAndPassword(String username, String password);
	void createNewUser(UserInfo userInfo, String password);
	User searchForUserByUserId(Integer userId);
}
