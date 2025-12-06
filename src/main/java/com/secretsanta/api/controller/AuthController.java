package com.secretsanta.api.controller;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dto.ErrorResponse;
import com.secretsanta.api.dto.LoginRequest;
import com.secretsanta.api.dto.LoginResponse;
import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.User;
import com.secretsanta.api.security.JwtTokenProvider;

/**
 * Authentication Controller for handling login/logout operations
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private JwtTokenProvider tokenProvider;

    @Resource
    private UserDao userDao;
    
    @Resource
    private SessionContext sessionContext;

    /**
     * Login endpoint
     * POST /api/auth/login
     * Request body: { "username": "user", "password": "pass", "edition": "edition" }
     * Response: { "token": "jwt-token", "type": "Bearer", "username": "user", "displayName": "User Name", "email": "user@example.com" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Set the edition in session context
            sessionContext.setSchema(loginRequest.getEdition());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Get user details
            User user = userDao.getUser(loginRequest.getUsername());

            // Return response with token and user info
            LoginResponse response = new LoginResponse(
                    jwt,
                    user.getUsername(),  // Use getUsername() from UserDetails
                    user.getDisplayName(),
                    user.getEmail()
            );

            request.getSession().setAttribute("CURRENT_USER", user.getUsername());
            
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during login", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body("{\"message\": \"Logged out successfully\"}");
    }

    /**
     * Get current authenticated user
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                
                String username = authentication.getName();
                User user = userDao.getUser(username);
                
                return ResponseEntity.ok(user);
            }
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving user information", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
