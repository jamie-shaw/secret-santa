package com.secretsanta.api.controller;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.context.Context;

import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.dto.EmailRequest;
import com.secretsanta.api.dto.EmailRequest.Addressee;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
@RequestMapping("/api")
@Tag(name = "Email", description = "Endpoints for sending email messages between Santa and recipients")
@SecurityRequirement(name = "bearerAuth")
public class EmailController {
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private EmailService emailService;
    
    @Operation(
        summary = "Send email message",
        description = "Sends an email message either to the user's Secret Santa recipient or to their Santa, depending on the addressee specified"
    )
    @PostMapping("/email/send")
    public void sendMessage(
            @ModelAttribute("CURRENT_USER") String currentUser, 
            @Parameter(description = "Email request with message and addressee", required = true)
            @RequestBody EmailRequest request) {
        
        String userMessage = request.getMessage();
        
        Context templateContext = new Context();
        templateContext.setVariable("userMessage", userMessage);
        
        FilterColumn filterColumn;
        String templateName;
        String subject;
        
        if (request.getAddressee() == Addressee.RECIPIENT) {
            // get the info for the current user
            filterColumn = FilterColumn.USER_NAME;
            subject = "A message from your Secret Santa";
            templateName = "messageToRecipient.html";
        } else {
            // get the info for the current user's santa
            filterColumn = FilterColumn.RECIPIENT;
            subject = "A message from your Secret Santa recipient";
            templateName = "messageFromRecipient.html";
        }
            
        User user = userDao.getUser(currentUser, filterColumn);
        
        emailService.sendEmail(user.getEmail(), subject, templateName, templateContext);
    }
    
}
