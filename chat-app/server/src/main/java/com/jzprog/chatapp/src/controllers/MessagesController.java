package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.advices.ControllerAdvice;
import com.jzprog.chatapp.src.model.*;
import com.jzprog.chatapp.src.services.MessagingService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private SimpMessagingTemplate template;
    
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
    public ResponseEntity<?> getMessages(@RequestParam("id") String id,
                                         @RequestParam("index") int index,
                                         @RequestHeader(value="Authorization") String authHeader) {
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));      
        User user = userService.searchForUserByUsername(username);
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.CONVERSATION_MEMBERSHIP, user, id);
        if (!validationResponse.isSuccess()) 
        	return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        List<MessageDTO> messages = new ArrayList<>();
    	for (Message mes : messagingService.fetchConversationMessages(Integer.valueOf(id), index, 20)) {
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
    public ResponseEntity<?> getConversations(@RequestHeader(value="Authorization") String authHeader,
                                              @RequestParam("page") int page) {
        String username = jwtUtil.getUsernameFromToken(authHeader.substring(7));  
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.USER_EXISTENCE, null, username);
        if (!validationResponse.isSuccess()) 
        	return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.UNAUTHORIZED);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        for (Conversation conv : messagingService.fetchUsersConversations(username, page)) {
           ConversationDTO conversationDTO = new ConversationDTO.ConversationBuilder()
        		    .withId(conv.getId())
        		    .withTitle(conv.getTitle())
        		    .withDate(conv.getCreatedDate())
        		    .withMembers(conv.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
                    .withOwnerId(conv.getOwnerId())
        		    .build();
           conversationDTOs.add(conversationDTO);
        }
        ConversationListDTO conversationListDTO = new ConversationListDTO();
        conversationListDTO.setConversationDTO(conversationDTOs);
        conversationListDTO.setTotal(userService.searchForUserByUsername(username).getConversations().size());
        return new ResponseEntity<>(conversationListDTO, HttpStatus.OK);
    }

    @ControllerAdvice
    @MessageMapping("/messages/{convId}")
    public void addMessage(@DestinationVariable String convId,
                           MessageDTO message) throws Exception {
        Date createdDate = new Date(System.currentTimeMillis());
        messagingService.addNewMessageToConversation(Integer.valueOf(convId), message.getText(), createdDate, message.getAuthorId());
        this.template.convertAndSend("/topic/conversation/" + convId, new MessageDTO.MessageBuilder()
        		.withText(message.getText())
        		.withAuthorId(message.getAuthorId())
        		.withAuthorUsername(userService.searchForUserByUserId(message.getAuthorId()).getUsername())
        		.withCreatedDate(createdDate)
                .withMessageType(SystemMessages.eventTypes.RECEIVE_MESSAGE.name())
        		.build());
    }
    
    @ControllerAdvice
    @MessageMapping("/src/{userId}")
    @SendTo("/topic/conversations")
    public ConversationDTO createConversation(@DestinationVariable String userId,
                                              ConversationDTO conv) throws Exception {
        Conversation newConversation;
        String eventType = SystemMessages.eventTypes.CONVERSATION_CREATED.name();
        if (conv.getId() != null) { // already existed conversation
            eventType = SystemMessages.eventTypes.MEMBER_ADDED.name();
            newConversation = messagingService.addMemberToConversation(conv.getId(), conv.getMembers()); // add new members
        }
        else newConversation = messagingService.createNewConversation(Integer.valueOf(userId), conv.getTitle(), new Date(System.currentTimeMillis()), conv.getMembers());
        return new ConversationDTO.ConversationBuilder()
        		.withId(newConversation.getId())
    		    .withTitle(newConversation.getTitle())
    		    .withDate(newConversation.getCreatedDate())
    		    .withMembers(newConversation.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
                .withOwnerId(Integer.valueOf(userId))
                .withMessagesCount(newConversation.getMessages().size())
    		    .withEventType(eventType)
    		    .build();
    }
    
    @ControllerAdvice
    @MessageMapping("/src/delete/{userId}") 
    @SendTo("/topic/conversations")
    public ConversationDTO deleteConversation(@DestinationVariable String userId,
                                              ConversationDTO conv) throws Exception {
        User user = userService.searchForUserByUserId(Integer.valueOf(userId));
        String eventType = SystemMessages.eventTypes.DELETE_CONVERSATION.name();
        Conversation existingConversation = messagingService.getExistingConversation(conv.getId());
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.CONVERSATION_OWNERSHIP, user, existingConversation);
        if (!validationResponse.isSuccess()) { // if is not the owner
            existingConversation = messagingService.removeConversationMember(conv.getId(), user); // leave from conversation
            eventType = SystemMessages.eventTypes.LEAVE_CONVERSATION.name();
        } else {
            messagingService.deleteConversation(conv.getId()); // delete entire conversation
        }
        return new ConversationDTO.ConversationBuilder()
        		.withId(conv.getId())
        		.withTitle(conv.getTitle())
                .withOwnerId(Integer.valueOf(userId))
                .withEventType(eventType)
                .withMembers(existingConversation.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
                .build();
    }

    @ControllerAdvice
    @MessageMapping("/messages/typing/{convId}")
    @SendTo("/topic/conversation/{convId}")
    public MessageDTO getTyping(@DestinationVariable String convId,
                                MessageDTO message) {
        return new MessageDTO.MessageBuilder()
                .withAuthorId(message.getAuthorId())
                .withAuthorUsername(userService.searchForUserByUserId(message.getAuthorId()).getUsername())
                .withMessageType(SystemMessages.eventTypes.TYPING.name())
                .build();
    }

}
