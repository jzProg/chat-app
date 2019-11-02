package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.MessageDTO;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.utils.JwtUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = "/getConversationMessages", method = RequestMethod.GET)
    public ResponseEntity<?> getMessages(@RequestParam("id") String id, @RequestHeader(value="Authorization") String authHeader) {   
    	List<MessageDTO> messages = new ArrayList<>();
    	for (Message mes : messagingService.fetchConversationMessages(Integer.valueOf(id))) {
            messages.add(new MessageDTO(mes.getText(), mes.getPostedBy()));
        }
        return new ResponseEntity<>(messages, HttpStatus.OK); 
    }
    
    @RequestMapping("/getConversations")
    public ResponseEntity<?> getConversations(@RequestHeader(value="Authorization") String authHeader) {      
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Conversation conv : messagingService.fetchUsersConversations(username)) {
           conversationDTOs.add(new ConversationDTO(conv.getId(), conv.getTitle(), conv.getCreatedDate()));
        }
        return new ResponseEntity<>(conversationDTOs, HttpStatus.OK);
    }

    @MessageMapping("/messages/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public MessageDTO addMessage(@DestinationVariable String convId, MessageDTO message) throws Exception {
        log.info("inside message topic!!!");
        Message newMessage = messagingService.addNewMessageToConversation(Integer.valueOf(convId), message.getText(), new Date(), message.getAuthorId());     
        return new MessageDTO(newMessage.getText(), newMessage.getPostedBy());
    }
    
    @MessageMapping("/src/{userId}")
    @SendTo("/topic/conversations/{userId}")
    public ConversationDTO createConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        Conversation newConversation =  messagingService.createNewConversation(Integer.valueOf(userId), conv.getTitle(), new Date());
        return new ConversationDTO(newConversation.getId(), newConversation.getTitle(), newConversation.getCreatedDate());
    }

}
