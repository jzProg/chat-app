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
	private String eventType;
	private Integer ownerId;
	
	public ConversationDTO() {}

    public ConversationDTO(ConversationBuilder builder) {
    	this.id = builder.id;
        this.title = builder.title;
        this.setDate(builder.date);
        this.eventType = builder.eventType;
        this.members = builder.members;
        this.messagesCount = builder.messagesCount;
        this.ownerId = builder.ownerId;
    }

    public ConversationDTO(Integer id, String title, Date date) {
    	this.id = id;
        this.title = title;
        this.setDate(date);
        this.members = new ArrayList<>();
        this.eventType = null;
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

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public static class ConversationBuilder {
		
		private Integer id;
		private String title;
		private Date date;
		private List<String> members;
		private int messagesCount;
		private String eventType;
		private Integer ownerId;
		
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
		
		public ConversationBuilder withEventType(String eventType) {
			this.eventType = eventType;
			return this;
		}

		public ConversationBuilder withMessagesCount(int count) {
			this.messagesCount = count;
			return this;
		}

		public ConversationBuilder withOwnerId(Integer ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		public ConversationDTO build() {
			return new ConversationDTO(this);
		}
	}
}
