package com.jzprog.chatapp.src.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
@Table(name="conversation", schema="public")
public class Conversation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String title;

    @Column(name = "created_date", columnDefinition="DATETIME")
    private Date createdDate;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "conversations")
    private Set<User> users = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="conversation", cascade = CascadeType.ALL)
    List<Message> messages = new ArrayList<>();

    protected Conversation() {}

    public Conversation(String title, Date createdDate) {
        this.title= title;
        this.createdDate = createdDate;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Integer getId() {
      return id;
    }

    public String getTitle() {
      return title;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
      }

    public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

    @Override
    public String toString() {
         return String.format(
                "Conversation[id=%d, title=%s, date=%s]",
               id, title, createdDate);
    }
}
