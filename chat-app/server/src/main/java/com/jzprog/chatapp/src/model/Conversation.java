package com.jzprog.chatapp.src.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Conversation {
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String title;	
    @Column(name = "created_date", columnDefinition="DATETIME")
    private Date createdDate;	  
    
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "conversations")
    private Set<User> users = new HashSet<>();

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

   @Override
    public String toString() {
         return String.format(
                "Conversation[id=%d, title=%s, date=%s]",
               id, title, createdDate);
    }
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
