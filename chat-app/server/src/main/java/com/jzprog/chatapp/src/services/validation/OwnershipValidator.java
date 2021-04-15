package com.jzprog.chatapp.src.services.validation;

import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.Validatable;
import com.jzprog.chatapp.src.utils.SystemMessages;
import org.springframework.stereotype.Service;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;

@Service
public class OwnershipValidator implements ValidationService {
    @Override
    public boolean validate(Validatable object, Object... extraInfo) {
        if (extraInfo == null) return false;
        User user = ((User) object);
        Conversation conv = ((Conversation) extraInfo[0]);
        return user.getId().equals(conv.getOwnerId());
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public SystemMessages.ValidationTypes getType() {
        return ValidationTypes.CONVERSATION_OWNERSHIP;
    }
}
