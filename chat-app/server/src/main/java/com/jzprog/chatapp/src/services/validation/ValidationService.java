package com.jzprog.chatapp.src.services.validation;

import com.jzprog.chatapp.src.model.Validatable;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;

public interface ValidationService {
	
	boolean validate(Validatable object, Object... extraInfo);
	String getErrorMessage();
    ValidationTypes getType();
}
