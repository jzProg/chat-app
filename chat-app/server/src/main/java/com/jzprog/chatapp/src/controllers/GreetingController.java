package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.model.HelloMessage;
import com.jzprog.chatapp.src.model.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import com.jzprog.chatapp.src.database.MessagesRepository;

@CrossOrigin(origins = "http://localhost:9090")
@RestController
public class GreetingController {

    Logger log = Logger.getLogger(GreetingController.class.getName());

    @Autowired
    MessagesRepository messageRepo;

    @RequestMapping("/api/getMessages")
    public ResponseEntity<?> getMessages() {
        log.info("inside getMessages controller!!!");
        List<Message> messages = (ArrayList<Message>) messageRepo.findAll();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @MessageMapping("/src")
    @SendTo("/topic/greetings")
    public Message greeting(HelloMessage message) throws Exception {
        log.info("inside greetings topic!!!");
        Message newMessage = new Message(message.getMessage(), 3);
        messageRepo.save(newMessage);
        return newMessage;
    }

}
