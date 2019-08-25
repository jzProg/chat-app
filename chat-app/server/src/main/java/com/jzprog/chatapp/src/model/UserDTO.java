package com.jzprog.chatapp.src.model;

public class UserDTO {
	private String username;
	private String token;
	private Integer userId;
	
	public UserDTO(Integer userId, String username, String token) {
		this.setUsername(username);
		this.setToken(token);
		this.setUserId(userId);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}
