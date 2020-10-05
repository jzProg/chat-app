package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.advices.ControllerAdvice;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.MessageDTO;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    UserService userService;
    
    @Autowired
    JwtUtil jwtUtil;

    @ControllerAdvice
    @GetMapping(value = "/getConversationMessages")
    public ResponseEntity<?> getMessages(@RequestParam("id") String id, @RequestHeader(value="Authorization") String authHeader) {
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        User user = userService.searchForUserByUsername(username);
        boolean userHasConversation = !(user.getConversations().stream().filter(c -> c.getId().equals(Integer.parseInt(id))).collect(Collectors.toList()).isEmpty());
    	if (!userHasConversation) return new ResponseEntity<>(SystemMessages.USER_NO_ACCESS, HttpStatus.UNAUTHORIZED); 
        List<MessageDTO> messages = new ArrayList<>();
    	for (Message mes : messagingService.fetchConversationMessages(Integer.valueOf(id))) {
            messages.add(new MessageDTO(mes.getText(), mes.getPostedBy(), userService.searchForUserByUserId(mes.getPostedBy()).getUsername(), mes.getCreatedDate()));
        }
        return new ResponseEntity<>(messages, HttpStatus.OK); 
    }
    
    @ControllerAdvice
    @RequestMapping("/getConversations")
    public ResponseEntity<?> getConversations(@RequestHeader(value="Authorization") String authHeader) {      
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Conversation conv : messagingService.fetchUsersConversations(username)) {
           conversationDTOs.add(new ConversationDTO(conv.getId(), conv.getTitle(), conv.getCreatedDate()));
        }
        return new ResponseEntity<>(conversationDTOs, HttpStatus.OK);
    }

    @ControllerAdvice
    @MessageMapping("/messages/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public MessageDTO addMessage(@DestinationVariable String convId, MessageDTO message) throws Exception {
        Date createdDate = new Date(System.currentTimeMillis());
        messagingService.addNewMessageToConversation(Integer.valueOf(convId), message.getText(), createdDate, message.getAuthorId());     
        return new MessageDTO(message.getText(), message.getAuthorId(), userService.searchForUserByUserId(message.getAuthorId()).getUsername(), createdDate);
    }
    
    @ControllerAdvice
    @MessageMapping("/src/{userId}")
    @SendTo("/topic/conversations")
    public ConversationDTO createConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        Conversation newConversation =  messagingService.createNewConversation(Integer.valueOf(userId), conv.getTitle(), new Date(System.currentTimeMillis()), conv.getMembers());
        ConversationDTO newConversationDTO = new ConversationDTO(newConversation.getId(), newConversation.getTitle(), newConversation.getCreatedDate());
        newConversationDTO.setMembers(conv.getMembers());
        return newConversationDTO;
    }
    
    @ControllerAdvice
    @MessageMapping("/src/delete/{userId}") 
    @SendTo("/topic/conversations")
    public ConversationDTO deleteConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        messagingService.deleteConversation(Integer.valueOf(conv.getId()));
        ConversationDTO deleted_conversation = new ConversationDTO(conv.getId(), conv.getTitle(), null);
        deleted_conversation.setDeleted(true);
        return deleted_conversation; 
    }

}
