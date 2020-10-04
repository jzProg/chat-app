package com.jzprog.chatapp.src.controllers;

import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.model.UserDTO;
import com.jzprog.chatapp.src.model.UserInfo;
import com.jzprog.chatapp.src.services.UserService;
import com.jzprog.chatapp.src.utils.AuthenticationUtils;
import com.jzprog.chatapp.src.utils.JwtUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "http://localhost:9090")
@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger log = Logger.getLogger(UserController.class.getName());
    
    @Autowired
	private AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;
    
    @Autowired
    UserDetailsService userDetailsService;
	
	@Autowired
	private JwtUtil jwtTokenUtil;

    @RequestMapping(value = "/auth", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> userAuth(@RequestBody UserInfo userInfo) throws Exception {   
       String hashedPassword = AuthenticationUtils.getHashedPassword(userInfo.getPassword());
       authenticate(userInfo.getUsername(), hashedPassword);
       final UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
	   final String token = jwtTokenUtil.generateToken(userDetails);
	   UserDTO userDTO = new UserDTO(userService.searchForUserByUsername(userInfo.getUsername()).getId(), userInfo.getUsername(), token);
       return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody UserInfo userInfo) throws Exception {
    	String hashedPassword = AuthenticationUtils.getHashedPassword(userInfo.getPassword());
        User user = userService.searchForUserByUsernameAndPassword(userInfo.getUsername(), hashedPassword);
        if (user == null) {
          log.info("User doesn't exist! About to register a new one...");
          userService.createNewUser(userInfo, hashedPassword);
          return new ResponseEntity<>("User succesfully created!", HttpStatus.OK);
        }
        return new ResponseEntity<>("User is already registered...", HttpStatus.FOUND);
    }
    
    @GetMapping("/getUsers") 
    public ResponseEntity<?> getUsersMatchingInput(@RequestParam("name") String inputName, @RequestHeader(value="Authorization") String authHeader) {
    	List<UserDTO> listOfUsers = new ArrayList<>();
        String username = jwtTokenUtil.getUsernameFromToken(authHeader.substring(7));      
    	if (!inputName.isEmpty()) {
    		for (User user : userService.searchForUsersMatchingString(inputName)) {
        		if (!user.getUsername().equals(username)) listOfUsers.add(new UserDTO(user.getId(), user.getUsername(), null));
             }	
    	}
        return new ResponseEntity<>(listOfUsers, HttpStatus.OK);
    }
    
    private void authenticate(String username, String password) throws Exception {
		try {
		  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
		   throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
		   throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
