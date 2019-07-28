package com.jzprog.chatapp.src.model;

public class Greeting {

//    private final long id;
    private final String content;

    public Greeting(String content) {//(long id, String content) {
      //  this.id = id;
        this.content = content;
    }

  //  public long getId() {
  //      return id;
  //  }

    public String getContent() {
        return content;
    }
}
