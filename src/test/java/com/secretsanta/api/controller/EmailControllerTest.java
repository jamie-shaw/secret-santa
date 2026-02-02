package com.secretsanta.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.thymeleaf.context.Context;

import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.dto.EmailRequest;
import com.secretsanta.api.dto.EmailRequest.Addressee;
import com.secretsanta.api.model.User;

class EmailControllerTest extends BaseControllerTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("john");
        testUser.setEmail("john@example.com");
        testUser.setDisplayName("John Doe");
    }

    @Test
    @WithMockUser(username = "john")
    void testSendMessageToRecipient() throws Exception {
        EmailRequest emailRequest = new EmailRequest(Addressee.RECIPIENT, "Hello, this is a test message!");
        
        when(userDao.getUser("john", FilterColumn.USER_NAME)).thenReturn(testUser);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(post("/api/email/send")
                .sessionAttr("CURRENT_USER", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk());

        verify(userDao, times(1)).getUser("john", FilterColumn.USER_NAME);
        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"),
            eq("A message from your Secret Santa"),
            eq("messageToRecipient.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "john")
    void testSendMessageToSanta() throws Exception {
        EmailRequest emailRequest = new EmailRequest(Addressee.SANTA, "Hey Santa, I have a question!");
        
        when(userDao.getUser("john", FilterColumn.RECIPIENT)).thenReturn(testUser);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(post("/api/email/send")
                .sessionAttr("CURRENT_USER", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk());

        verify(userDao, times(1)).getUser("john", FilterColumn.RECIPIENT);
        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"),
            eq("A message from your Secret Santa recipient"),
            eq("messageFromRecipient.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "jane")
    void testSendMessageWithDifferentUser() throws Exception {
        User janeUser = new User("jane");
        janeUser.setEmail("jane@example.com");
        janeUser.setDisplayName("Jane Smith");
        
        EmailRequest emailRequest = new EmailRequest(Addressee.RECIPIENT, "Message from Jane");
        
        when(userDao.getUser("jane", FilterColumn.USER_NAME)).thenReturn(janeUser);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(post("/api/email/send")
                .sessionAttr("CURRENT_USER", "jane")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk());

        verify(userDao, times(1)).getUser("jane", FilterColumn.USER_NAME);
        verify(emailService, times(1)).sendEmail(
            eq("jane@example.com"),
            eq("A message from your Secret Santa"),
            eq("messageToRecipient.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "john")
    void testSendMessageWithLongContent() throws Exception {
        String longMessage = "This is a very long message that contains a lot of text. ".repeat(10);
        EmailRequest emailRequest = new EmailRequest(Addressee.SANTA, longMessage);
        
        when(userDao.getUser("john", FilterColumn.RECIPIENT)).thenReturn(testUser);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(post("/api/email/send")
                .sessionAttr("CURRENT_USER", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"),
            anyString(),
            anyString(),
            any(Context.class)
        );
    }
}
