package com.jzprog.chatapp.src.model;

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

    protected Message() {}

    public Message(String text, Integer postedBy) {
        this.text= text;
        this.postedBy= postedBy;
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
                "Message[id=%d, text='%s', postedBy='%s']",
               id, text, postedBy);
    }

}
