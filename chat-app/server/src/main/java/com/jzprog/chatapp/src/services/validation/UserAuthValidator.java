package com.jzprog.chatapp.src.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserInfo;
import com.jzprog.chatapp.src.model.Validatable;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.utils.SystemMessages;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;

@Service
public class UserAuthValidator implements ValidationService {
	
	@Autowired
    private UserService userService;

	@Override
	public boolean validate(Validatable object, Object... extraInfo) {
		if (object == null) {
			if (extraInfo != null) {
			  String username = ((String) extraInfo[0]);
			  User user = userService.searchForUserByUsername(username);
			  return user != null;
			} 
			return false;
		} 
		UserInfo userInfo = ((UserInfo) object);
		User user = userService.searchForUserByUsername(userInfo.getUsername());
		return user != null;	
	}

	@Override
	public String getErrorMessage() {
		return SystemMessages.USER_NOT_EXIST;
	}

	@Override
	public ValidationTypes getType() {
		return ValidationTypes.USER_EXISTENCE;
	}
}
