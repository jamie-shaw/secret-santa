package com.secretsanta.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dto.LoginRequest;
import com.secretsanta.api.dto.LoginResponse;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.RequestContext;
import com.secretsanta.api.model.User;
import com.secretsanta.api.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Authentication Controller for handling login/logout operations
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;
    
    @Resource
    private JwtTokenProvider tokenProvider;
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private RecipientDao recipientDao;
    
    @Operation(
        summary = "User login",
        description = "Authenticate a user with username, password, and edition. Returns a JWT token for authenticated requests."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // Set the schema/edition in the request context
        RequestContext.setSchema(loginRequest.getEdition());
        
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        // Get user details
        User user = userDao.getUser(loginRequest.getUsername());
        
        Recipient recipient = recipientDao.getRecipientForCurrentUser(user.getUsername());
        
        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication, loginRequest, recipient);
        
        // Return response with token and user info
        LoginResponse response = new LoginResponse(
                jwt,
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "User logout",
        description = "Logs out the current user by clearing the security context"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("{\"message\": \"Logged out successfully\"}");
    }
}
