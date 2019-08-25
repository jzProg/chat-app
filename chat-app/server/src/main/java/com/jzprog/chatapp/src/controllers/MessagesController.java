package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.utils.JwtUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;


@CrossOrigin(origins = "http://localhost:9090")
@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    Logger log = Logger.getLogger(MessagesController.class.getName());
    
    @Autowired
    MessagingService messagingService;
    
    @Autowired
    JwtUtil jwtUtil;

    /*@RequestMapping("/getMessages") // todo implement correctly
    public ResponseEntity<?> getMessages(@RequestHeader(value="Authorization") String authHeader) {      
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));
        User user = (User) userRepo.findUserByUsername(username);
        List<Message> messages = (ArrayList<Message>) messageRepo.findByPostedBy(user.getId());
        return new ResponseEntity<>(messages, HttpStatus.OK);
    } */
    
    @RequestMapping("/getConversations")
    public ResponseEntity<?> getConversations(@RequestHeader(value="Authorization") String authHeader) {      
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Conversation conv : messagingService.fetchUsersConversations(username)) {
           conversationDTOs.add(new ConversationDTO(conv.getTitle(), conv.getCreatedDate()));
        }
        return new ResponseEntity<>(conversationDTOs, HttpStatus.OK);
    }

    /*@MessageMapping("/src")  // todo implement correctly
    @SendTo("/topic/message")
    public Message addMessage(MessageDTO message) throws Exception {
        log.info("inside message topic!!!");
        Message newMessage = new Message(message.getMessage(), 3);
        messageRepo.save(newMessage);
        return newMessage;
    } */
    
    @MessageMapping("/src/{userId}")
    @SendTo("/topic/conversations/{userId}")
    public ConversationDTO createConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        Conversation newConversation =  messagingService.createNewConversation(Integer.valueOf(userId), conv.getTitle(), new Date());
        return new ConversationDTO(newConversation.getTitle(), newConversation.getCreatedDate());
    }

}
