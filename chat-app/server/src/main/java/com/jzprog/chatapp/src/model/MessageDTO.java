package com.jzprog.chatapp.src.model;

public class MessageDTO {

    private String text;
    private Integer authorId;

    public MessageDTO() {
    }

    public MessageDTO(String message, Integer authorId) {
        this.text = message;
        this.authorId = authorId;
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
}
