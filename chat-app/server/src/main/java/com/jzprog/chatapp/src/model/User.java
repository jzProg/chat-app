package com.jzprog.chatapp.src.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String email;

    protected User() {}

    public User(String username, String password, String email) {
        this.username= username;
        this.password= password;
        this.email = email;
    }

    public Integer getId() {
      return id;
    }

    public String getEmail() {
      return email;
    }

    public String getPassword() {
      return password;
    }

    public String getUsername() {
      return username;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public void setEmail(String email) {
      this.email = email;
    }

   @Override
    public String toString() {
         return String.format(
                "User[id=%d, username='%s', password='%s', email=%s]",
               id, username, password, email);
    }
}
