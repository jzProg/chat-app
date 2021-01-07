package com.jzprog.chatapp.src.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationDTO {
	
	private Integer id;
	private String title;
	private Date date;
	private List<String> members;
	private int messagesCount;
	private boolean deleted;
	
	public ConversationDTO() {}

    public ConversationDTO(ConversationBuilder builder) {
    	this.id = builder.id;
        this.title = builder.title;
        this.setDate(builder.date);
        this.setDeleted(builder.deleted);
        this.members = builder.members;
        this.messagesCount = builder.messagesCount;
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

	public int getMessagesCount() {
		return messagesCount;
	}

	public void setMessagesCount(int messagesCount) {
		this.messagesCount = messagesCount;
	}

	public static class ConversationBuilder {
		
		private Integer id;
		private String title;
		private Date date;
		private List<String> members;
		private int messagesCount;
		private boolean deleted;

		
		public ConversationBuilder withId(Integer id) {
			this.id = id;
			return this;
		}
		
		public ConversationBuilder withTitle(String title) {
			this.title = title;
			return this;
		}
		
		public ConversationBuilder withDate(Date date) {
			this.date = date;
			return this;
		}
		
		public ConversationBuilder withMembers(List<String> members) {
			this.members = members;
			return this;
		}
		
		public ConversationBuilder withDeleted(boolean deleted) {
			this.deleted = deleted;
			return this;
		}

		public ConversationBuilder withMessagesCount(int count) {
			this.messagesCount = count;
			return this;
		}
		
		public ConversationDTO build() {
			return new ConversationDTO(this);
		}
	}
}
