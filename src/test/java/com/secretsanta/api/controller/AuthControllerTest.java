package com.secretsanta.api.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

import com.secretsanta.api.dto.LoginRequest;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

class AuthControllerTest extends BaseControllerTest {

    private LoginRequest loginRequest;
    private User testUser;
    private Recipient testRecipient;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("john");
        loginRequest.setPassword("password123");
        loginRequest.setEdition("shaw");

        testUser = new User("john");
        testUser.setEmail("john@example.com");
        testUser.setDisplayName("John Doe");

        testRecipient = new Recipient("john", "2026", "jane", true, true);

        authentication = mock(Authentication.class);
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class), any(LoginRequest.class), any(Recipient.class)))
            .thenReturn("mock-jwt-token");
        when(userDao.getUser("john")).thenReturn(testUser);
        when(recipientDao.getRecipientForCurrentUser("john")).thenReturn(testRecipient);
        doNothing().when(sessionContext).setSchema(anyString());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", is("mock-jwt-token")))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.displayName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(sessionContext, times(1)).setSchema("shaw");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).generateToken(any(Authentication.class), any(LoginRequest.class), any(Recipient.class));
        verify(userDao, times(1)).getUser("john");
        verify(recipientDao, times(1)).getRecipientForCurrentUser("john");
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
        doNothing().when(sessionContext).setSchema(anyString());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Invalid username or password")))
                .andExpect(jsonPath("$.status", is(401)));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    void testLoginWithDifferentEdition() throws Exception {
        loginRequest.setEdition("fernald");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class), any(LoginRequest.class), any(Recipient.class)))
            .thenReturn("mock-jwt-token-fernald");
        when(userDao.getUser("john")).thenReturn(testUser);
        when(recipientDao.getRecipientForCurrentUser("john")).thenReturn(testRecipient);
        doNothing().when(sessionContext).setSchema(anyString());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token-fernald")));

        verify(sessionContext, times(1)).setSchema("fernald");
    }

    @Test
    void testLoginWithException() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Unexpected error"));
        doNothing().when(sessionContext).setSchema(anyString());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("An error occurred during login")))
                .andExpect(jsonPath("$.status", is(500)));
    }

    @Test
    @WithMockUser(username = "john")
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.message", is("Logged out successfully")));
    }

    @Test
    void testLoginWithMissingFields() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("john");
        // Missing password and edition

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testLoginWithEmptyUsername() throws Exception {
        loginRequest.setUsername("");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
        doNothing().when(sessionContext).setSchema(anyString());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
