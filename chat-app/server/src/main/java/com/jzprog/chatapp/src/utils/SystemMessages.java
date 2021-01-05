package com.jzprog.chatapp.src.utils;

public class SystemMessages {
	
	public static final String DEFAULT_CONVERSATION_NAME = "Unamed";
	
	public static final String USER_CREATED ="User succesfully created!";
	public static final String USER_ALREADY_REGISTERED = "User is already registered...";
	public static final String USER_NOT_EXIST = "User doesn't exist!";
	public static final String USER_NO_ACCESS = "User doesn't have access to this conversation...";
	
	public static final String IMAGE_FILE_UPLOAD_ERROR = "File Upload Failure: no image sent!";
	public static final String IMAGE_FILE_UPLOAD_EXCEPTION = "File Upload Failure: exception on file save!";
	public static final String IMAGE_FILE_UPLOAD_SIZE_ERROR = "File Too large! Please choose a smaller image";

	public static final String PUSH_NOTIFICATION_DELETED = "Push Notification Sub Info successfully deleted!";
	public static final String PUSH_NOTIFICATION_SAVE_ERROR = "Push Notification Sub Info failed to be saved...User doesn't exist...";
	public static final String PUSH_NOTIFICATION_SENT = "Push Notification broadcast just sent!";
	public static final String PUSH_NOTIFICATION_SUBJECT = "Placeholder subject";
	
	public static final String USER_DISABLED = "USER_DISABLED";
	public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
	
	public enum NotificationCategories {
		USER_LOGGED_IN ("User is active", "User %s just logged in!", "/static/logo.png"),
		MESSAGE_RECEIVED("Message received", "User %s sent you a message!", "/static/logo.png");
		
		private String title, message, image;
		
		NotificationCategories(String title, String message, String image) {
			this.title = title;
			this.message = message;
			this.image = image;
		}

		public String getTitle() {
			return title;
		}

		public String getMessage() {
			return message;
		}

		public String getImage() {
			return image;
		}
	}
	
	public enum ValidationTypes {
		USER_EXISTENCE,
		CONVERSATION_OWNERSHIP,
		REGISTRATION_CHECK
	}
}
