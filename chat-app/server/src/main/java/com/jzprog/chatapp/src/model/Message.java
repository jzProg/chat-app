package com.jzprog.chatapp.src.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;

@Entity
@Table(name="message", schema="public")
public class Message {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String text;

    private Integer postedBy;

    @Column(name = "created_date", columnDefinition="DATETIME")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    protected Message() {}

    public Message(String text, Integer postedBy, Date createdDate) {
        this.text= text;
        this.postedBy= postedBy;
        this.createdDate = createdDate;
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
               id, text, postedBy, createdDate, conversation);
    }

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
