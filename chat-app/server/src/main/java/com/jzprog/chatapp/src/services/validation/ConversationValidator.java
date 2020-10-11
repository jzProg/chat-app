package com.jzprog.chatapp.src.services.validation;

import java.util.stream.Collectors;
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
		return !(user.getConversations().stream().filter(c -> c.getId().equals(Integer.parseInt(id))).collect(Collectors.toList()).isEmpty());
	}

	@Override
	public String getErrorMessage() {
		return SystemMessages.USER_NO_ACCESS;
	}

	@Override
	public ValidationTypes getType() {
		return ValidationTypes.CONVERSATION_OWNERSHIP;
	}
}
