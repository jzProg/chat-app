package com.jzprog.chatapp.src.model;

public class UserDTO {
	
	private String username;
	private String token;
	private Integer userId;
	private String email;
	private byte[] image;
	
	public UserDTO() {}
	
	public UserDTO(UserBuilder builder) {
		this.setUsername(builder.username);
		this.setToken(builder.token);
		this.setUserId(builder.userId);
		this.setImage(builder.image);
		this.setEmail(builder.email);
	}
	
	public UserDTO(Integer userId, String username, String token, byte[] image) {
		this.setUsername(username);
		this.setToken(token);
		this.setUserId(userId);
		this.setImage(image);
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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static class UserBuilder {
		
		private String username;
		private String token;
		private Integer userId;
		private byte[] image;
		private String email;
		
		public UserBuilder withUsername(String username) {
			this.username = username;
			return this;
		}
		
		public UserBuilder withToken(String token) {
			this.token = token;
			return this;
		}
		
		public UserBuilder withUserId(Integer userId) {
			this.userId = userId;
			return this;
		}
		
		public UserBuilder withImage(byte[] image) {
			this.image = image;
			return this;
		}

		public UserBuilder withEmail(String email) {
			this.email = email;
			return this;
		}
		
		public UserDTO build() {
			return new UserDTO(this);
		}
	}
}
