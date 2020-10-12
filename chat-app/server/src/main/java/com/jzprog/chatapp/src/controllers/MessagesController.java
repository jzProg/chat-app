package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.advices.ControllerAdvice;
import com.jzprog.chatapp.src.model.Conversation;
import com.jzprog.chatapp.src.model.ConversationDTO;
import com.jzprog.chatapp.src.model.Message;
import com.jzprog.chatapp.src.model.MessageDTO;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.ValidationResponse;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    
    @Autowired
    private MessagingService messagingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ValidationStrategy validationStrategy;

    @ControllerAdvice
    @GetMapping(value = "/getConversationMessages")
    public ResponseEntity<?> getMessages(@RequestParam("id") String id, @RequestHeader(value="Authorization") String authHeader) {
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        User user = userService.searchForUserByUsername(username);
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.CONVERSATION_OWNERSHIP, user, id);
        if (!validationResponse.isSuccess()) 
        	return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        List<MessageDTO> messages = new ArrayList<>();
    	for (Message mes : messagingService.fetchConversationMessages(Integer.valueOf(id))) {
            messages.add(new MessageDTO.MessageBuilder()
            		.withText(mes.getText())
            		.withAuthorId(mes.getPostedBy())
            		.withAuthorUsername(userService.searchForUserByUserId(mes.getPostedBy()).getUsername())
            		.withCreatedDate(mes.getCreatedDate())
            		.build());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK); 
    }
    
    @ControllerAdvice
    @RequestMapping("/getConversations")
    public ResponseEntity<?> getConversations(@RequestHeader(value="Authorization") String authHeader) {      
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));  
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.USER_EXISTENCE, null, username);
        if (!validationResponse.isSuccess()) 
        	return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Conversation conv : messagingService.fetchUsersConversations(username)) {
           ConversationDTO conversationDTO = new ConversationDTO.ConversationBuilder()
        		    .withId(conv.getId())
        		    .withTitle(conv.getTitle())
        		    .withDate(conv.getCreatedDate())
        		    .withMembers(conv.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
        		    .withDeleted(false)
        		    .build();
           conversationDTOs.add(conversationDTO);
        }
        return new ResponseEntity<>(conversationDTOs, HttpStatus.OK);
    }

    @ControllerAdvice
    @MessageMapping("/messages/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public MessageDTO addMessage(@DestinationVariable String convId, MessageDTO message) throws Exception {
        Date createdDate = new Date(System.currentTimeMillis());
        messagingService.addNewMessageToConversation(Integer.valueOf(convId), message.getText(), createdDate, message.getAuthorId());     
        return new MessageDTO.MessageBuilder()
        		.withText(message.getText())
        		.withAuthorId(message.getAuthorId())
        		.withAuthorUsername(userService.searchForUserByUserId(message.getAuthorId()).getUsername())
        		.withCreatedDate(createdDate)
        		.build();
    }
    
    @ControllerAdvice
    @MessageMapping("/src/{userId}")
    @SendTo("/topic/conversations")
    public ConversationDTO createConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        Conversation newConversation =  messagingService.createNewConversation(Integer.valueOf(userId), conv.getTitle(), new Date(System.currentTimeMillis()), conv.getMembers());
        ConversationDTO newConversationDTO = new ConversationDTO.ConversationBuilder()
        		.withId(newConversation.getId())
    		    .withTitle(newConversation.getTitle())
    		    .withDate(newConversation.getCreatedDate())
    		    .withMembers(conv.getMembers())
    		    .withDeleted(false)
    		    .build();
        return newConversationDTO;
    }
    
    @ControllerAdvice
    @MessageMapping("/src/delete/{userId}") 
    @SendTo("/topic/conversations")
    public ConversationDTO deleteConversation(@DestinationVariable String userId, ConversationDTO conv) throws Exception {
        messagingService.deleteConversation(Integer.valueOf(conv.getId()));
        ConversationDTO deleted_conversation = new ConversationDTO.ConversationBuilder()
        		.withId(conv.getId())
        		.withTitle( conv.getTitle())
      		    .withDeleted(true)
      		    .build();
        deleted_conversation.setDeleted(true);
        return deleted_conversation; 
    }

}
