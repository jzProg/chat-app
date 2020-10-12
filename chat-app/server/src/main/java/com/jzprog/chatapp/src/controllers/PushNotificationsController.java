package com.jzprog.chatapp.src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.jzprog.chatapp.src.advices.ControllerAdvice;
import com.jzprog.chatapp.src.model.NotificationDTO;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.services.PushNotificationService;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;

@CrossOrigin(origins = "http://localhost:9090")
@RestController
@RequestMapping("/api/notifications")
public class PushNotificationsController {
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
    @Autowired
	private UserService userService;
    
    @Autowired
	private PushNotificationService pushNotificationService;
	
	@ControllerAdvice
    @RequestMapping(value = "/sendLoginEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> broadcastLogin(@RequestHeader(value="Authorization") String authHeader) throws Exception {  
       String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));  
       User user = userService.searchForUserByUsername(username);
	   pushNotificationService.sendPushNotificationToObservers(user, SystemMessages.NotificationCategories.USER_LOGGED_IN);
       return new ResponseEntity<>(SystemMessages.PUSH_NOTIFICATION_SENT, HttpStatus.OK);
    }
	
	@ControllerAdvice
    @RequestMapping(value = "/sendPushSubInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> storePushInfo(@RequestBody NotificationDTO notificationDTO, 
    		                               @RequestHeader(value="Authorization") String authHeader) throws Exception {   
       String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));     
       User user  = userService.searchForUserByUsername(username);
       if (user != null) {
    	   Integer userId = user.getId();
    	   String infoObject = notificationDTO.getPushSubInfo();
    	   Integer newEntryId = pushNotificationService.storePushNotificationSubInfo(infoObject, userId);
    	   return new ResponseEntity<>(newEntryId, HttpStatus.OK);
       }
       return new ResponseEntity<>(SystemMessages.PUSH_NOTIFICATION_SAVE_ERROR, HttpStatus.NO_CONTENT);
    }
	
	@ControllerAdvice
    @RequestMapping(value = "/removePushSubInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> deletePushSubInfo(@RequestBody NotificationDTO notificationDTO, 
    		                                   @RequestHeader(value="Authorization") String authHeader) throws Exception {      
		Integer subId = notificationDTO.getSubId();
		pushNotificationService.deletePushNotificationSubInfo(subId);
        return new ResponseEntity<>(SystemMessages.PUSH_NOTIFICATION_DELETED, HttpStatus.OK);
    }
	
	@ControllerAdvice
    @GetMapping("/getPublicKey")
    public ResponseEntity<?> fetchPublicKey() {        
        return new ResponseEntity<>(pushNotificationService.providePublicKey(), HttpStatus.OK);
    }

}
