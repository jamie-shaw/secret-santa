package com.secretsanta.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.context.Context;

import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.EmailService;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class EmailController extends BaseController {
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private EmailService emailService;
    
    @GetMapping("/email")
    public String showEmail() {
        return "email";
    }
    
    @PostMapping("/email")
    public String sendMessage(@ModelAttribute("CURRENT_USER") String currentUser,
                               HttpServletRequest request,
                               Model model) {
        
        String userMessage = request.getParameter("message");
        
        Context templateContext = new Context();
        templateContext.setVariable("userMessage", userMessage);
        
        FilterColumn filterColumn;
        String templateName;
        String subject;
        
        if (request.getParameter("to").equals("recipient")) {
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
        
        setSuccessMessage(request, "Your message has been sent.");
       
        return "redirect:/home";
        
    }
    
}
