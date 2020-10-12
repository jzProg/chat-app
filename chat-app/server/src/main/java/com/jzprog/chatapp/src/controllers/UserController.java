package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.advices.ControllerAdvice;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserDTO;
import com.jzprog.chatapp.src.model.UserInfo;
import com.jzprog.chatapp.src.model.ValidationResponse;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.services.validation.ValidationStrategy;
import com.jzprog.chatapp.src.utils.AuthenticationUtils;
import com.jzprog.chatapp.src.utils.JwtUtil;
import com.jzprog.chatapp.src.utils.SystemMessages;
import com.jzprog.chatapp.src.utils.SystemMessages.ValidationTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger log = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
    private ValidationStrategy validationStrategy;

	@ControllerAdvice
    @RequestMapping(value = "/auth", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> userAuth(@RequestBody UserInfo userInfo) throws Exception {   
       ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.USER_EXISTENCE, userInfo);
       if (!validationResponse.isSuccess()) {
           return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.UNAUTHORIZED);
       }
       String hashedPassword = AuthenticationUtils.getHashedPassword(userInfo.getPassword());
       jwtTokenUtil.authenticate(userInfo.getUsername(), hashedPassword);
       final UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
	   final String token = jwtTokenUtil.generateToken(userDetails);
	   User user = userService.searchForUserByUsername(userInfo.getUsername());
	   UserDTO userDTO = new UserDTO.UserBuilder()
		             .withUserId(user.getId())
		             .withUsername(userInfo.getUsername())
		             .withToken(token)
		             .withImage(user.getImage())
		             .build();
       return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @ControllerAdvice
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody UserInfo userInfo) throws Exception {
    	String hashedPassword = AuthenticationUtils.getHashedPassword(userInfo.getPassword());
        ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.REGISTRATION_CHECK, userInfo);
        if (validationResponse.isSuccess()) {
          log.info(SystemMessages.USER_NOT_EXIST);
          userService.createNewUser(userInfo, hashedPassword);
          return new ResponseEntity<>(SystemMessages.USER_CREATED, HttpStatus.OK);
        }
        return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.FOUND);
    }
    
    @ControllerAdvice
    @GetMapping("/getUsers") 
    public ResponseEntity<?> getUsersMatchingInput(@RequestParam("name") String inputName, 
    		                                       @RequestHeader(value="Authorization") String authHeader) {
    	List<UserDTO> listOfUsers = new ArrayList<>();
        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));      
    	if (!inputName.isEmpty()) {
    		for (User user : userService.searchForUsersMatchingString(inputName)) {
        		if (!user.getUsername().equals(username)) listOfUsers.add(new UserDTO.UserBuilder().withUserId(user.getId()).withUsername(user.getUsername()).build());
             }	
    	}
        return new ResponseEntity<>(listOfUsers, HttpStatus.OK);
    }
    
    @ControllerAdvice
    @RequestMapping(value = "/updateProfileImage", method = RequestMethod.POST)
    public ResponseEntity<?> updateProfileImage(@RequestParam("imageFile") MultipartFile file, 
    		                                    @RequestParam("username") String username) {
      ValidationResponse validationResponse = validationStrategy.provideValidation(ValidationTypes.USER_EXISTENCE, null, username);
      if (!validationResponse.isSuccess()) {
    	  return new ResponseEntity<>(validationResponse.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      if (file.isEmpty()) {
    	  return new ResponseEntity<>(SystemMessages.IMAGE_FILE_UPLOAD_ERROR, HttpStatus.NO_CONTENT);
      }
      try {
    	  User user = userService.updateProfileImage(username, file.getBytes());
    	  UserDTO userDTO = new UserDTO.UserBuilder()
		    			  .withUserId(user.getId())
		    			  .withUsername(user.getUsername())
		    			  .withImage(user.getImage())
		    			  .build();
          return new ResponseEntity<>(userDTO, HttpStatus.OK);
      } catch (IOException e) {
          log.warning(e.getMessage());
          return new ResponseEntity<>(SystemMessages.IMAGE_FILE_UPLOAD_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
}
