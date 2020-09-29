package com.jzprog.chatapp.src.model;

import java.util.Date;

public class MessageDTO {

    private String text;
    private Integer authorId;
    private String authorUsername;
    private Date createdDate;

    public MessageDTO() {
    }

    public MessageDTO(String message, Integer authorId, String authorUsername, Date createdDate) {
        this.text = message;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.createdDate = createdDate;
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
	
	
}
