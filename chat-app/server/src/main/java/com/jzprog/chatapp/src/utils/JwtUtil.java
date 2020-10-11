package com.jzprog.chatapp.src.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	
	public String getUsernameFromToken(String token) {
	  return getClaimFromToken(token, Claims::getSubject);
	}
	
	
	public Date getExpirationDateFromToken(String token) {
	  return getClaimFromToken(token, Claims::getExpiration);
	}
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	  final Claims claims = getAllClaimsFromToken(token);
	  return claimsResolver.apply(claims);
	}
	
	
	private Claims getAllClaimsFromToken(String token) {
	  return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	
	private Boolean isTokenExpired(String token) {
	  final Date expiration = getExpirationDateFromToken(token);
	  return expiration.before(new Date());
	}
	
	public String generateToken(UserDetails userDetails) {
	  Map<String, Object> claims = new HashMap<>();
	  return doGenerateToken(claims, userDetails.getUsername());
	}
	
	
	private String doGenerateToken(Map<String, Object> claims, String subject) {
	  return Jwts.builder()
			     .setClaims(claims)
			     .setSubject(subject)
			     .setIssuedAt(new Date(System.currentTimeMillis()))
	             .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
	             .signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
	  final String username = getUsernameFromToken(token);
	  return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	public void authenticate(String username, String password) throws Exception {
		try {
		  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
		   throw new Exception(SystemMessages.USER_DISABLED, e);
		} catch (BadCredentialsException e) {
		   throw new Exception(SystemMessages.INVALID_CREDENTIALS, e);
		}
	}

}
