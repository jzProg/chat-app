package com.jzprog.chatapp.src.model;

import java.util.Date;

public class MessageDTO {

    private String text;
    private Integer authorId;
    private String authorUsername;
	private String messageType;
    private Date createdDate;

    public MessageDTO() {}

    public MessageDTO(MessageBuilder builder) {
    	 this.text = builder.text;
         this.authorId = builder.authorId;
         this.authorUsername = builder.authorUsername;
         this.createdDate = builder.createdDate;
		 this.messageType = builder.messageType;
    }

    public MessageDTO(String message, Integer authorId, String authorUsername, Date createdDate) {
        this.text = message;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.createdDate = createdDate;
		this.messageType = null;
	}

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public static class MessageBuilder {
		
		private String text;
	    private Integer authorId;
	    private String authorUsername;
	    private Date createdDate;
		private String messageType;

		public MessageBuilder withText(String text) {
			this.text = text;
			return this;
		}
		
		public MessageBuilder withAuthorId(Integer authorId) {
			this.authorId = authorId;
			return this;
		}
		
		public MessageBuilder withAuthorUsername(String authorUsername) {
			this.authorUsername = authorUsername;
			return this;
		}
		
		public MessageBuilder withCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
			return this;
		}

		public MessageBuilder withMessageType(String messageType) {
			this.messageType = messageType;
			return this;
		}
		
		public MessageDTO build() {
			return new MessageDTO(this);
		}
	}
}
