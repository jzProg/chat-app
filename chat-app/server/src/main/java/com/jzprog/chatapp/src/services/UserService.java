package com.jzprog.chatapp.src.services;

import java.util.List;

import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;

public interface UserService {
	User searchForUserByUsername(String username);
	void createNewUser(UserInfo userInfo, String password);
	User searchForUserByUserId(Integer userId);
	List<User> searchForUsersMatchingString(String str);
	User updateProfileImage(String username, byte[] newImagePath);
}
