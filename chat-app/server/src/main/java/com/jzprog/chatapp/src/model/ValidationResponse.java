package com.jzprog.chatapp.src.model;

public class ValidationResponse {
	
	private boolean isSuccess;
	private String errorMessage;
	
	public ValidationResponse(boolean result, String errorMessage) {
		setSuccess(result);
		setErrorMessage(errorMessage);
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
