package com.jzprog.chatapp.src.services.validation;

import org.springframework.stereotype.Service;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.Validatable;
import com.jzprog.chatapp.src.utils.SystemMessages;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;

@Service
public class ConversationValidator implements ValidationService {

	@Override
	public boolean validate(Validatable object, Object... extraInfo) {
		if (extraInfo == null) return false;
		User user = ((User) object);
		String id = ((String) extraInfo[0]);
		return user.getConversations().stream().anyMatch(c -> c.getId().equals(Integer.parseInt(id)));
	}

	@Override
	public String getErrorMessage() {
		return SystemMessages.USER_NO_ACCESS;
	}

	@Override
	public ValidationTypes getType() {
		return ValidationTypes.CONVERSATION_MEMBERSHIP;
	}
}
