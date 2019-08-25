package com.jzprog.chatapp.src.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String text;
    private Integer postedBy;
    private Date createdDate;
    private Integer conversationId;

    protected Message() {}

    public Message(String text, Integer postedBy, Date createdDate, Integer conversationId) {
        this.text= text;
        this.postedBy= postedBy;
        this.createdDate = createdDate;
        this.conversationId = conversationId;
    }

    public Integer getId() {
      return id;
    }

    public Integer getPostedBy() {
      return postedBy;
    }

    public String getText() {
      return text;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public void setPostedBy(Integer postedBy) {
      this.postedBy = postedBy;
    }

    public void setText(String text) {
      this.text = text;
    }

   @Override
    public String toString() {
         return String.format(
                "Message[id=%d, text=%s, postedBy=%s, date=%s, conversation=%s]",
               id, text, postedBy, createdDate, conversationId);
    }

	public Integer getConversationId() {
		return conversationId;
	}
	
	public void setConversationId(Integer conversationId) {
		this.conversationId = conversationId;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
