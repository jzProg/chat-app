package com.jzprog.chatapp.src.model;

import java.util.Date;

public class ConversationDTO {
	private Integer id;
	private String title;
	private Date date;

    public ConversationDTO() {
    }

    public ConversationDTO(Integer id, String title, Date date) {
    	this.id = id;
        this.title = title;
        this.setDate(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
