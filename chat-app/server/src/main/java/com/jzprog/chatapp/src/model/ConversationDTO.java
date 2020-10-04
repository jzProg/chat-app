package com.jzprog.chatapp.src.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationDTO {
	private Integer id;
	private String title;
	private Date date;
	private List<String> members;
	private boolean deleted;

    public ConversationDTO() {
    }

    public ConversationDTO(Integer id, String title, Date date) {
    	this.id = id;
        this.title = title;
        this.setDate(date);
        this.setDeleted(false);
        this.members = new ArrayList<>();
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	

}
