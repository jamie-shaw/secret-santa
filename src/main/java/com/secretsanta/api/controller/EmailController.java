package com.secretsanta.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.EmailService;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class EmailController extends BaseController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/email")
    public String showEmail() {
        return "email";
    }
    
    @PostMapping("/email")
    public String sendMessage(@ModelAttribute("CURRENT_USER") String currentUser,
                               HttpServletRequest request,
                               Model model) {
        
        FilterColumn filterColumn;
        String message = request.getParameter("message");
        String subject;
        
        if (request.getParameter("to").equals("recipient")) {
            // Get the info for the current user
            filterColumn = FilterColumn.USER_NAME;
            subject = "A message from your Secret Santa";
        } else {
            // Get the info for the current user's santa
            filterColumn = FilterColumn.RECIPIENT;
            subject = "A message from your Secret Santa recipient";
        }
            
        User user = userDao.getUser(currentUser, filterColumn);
        
        emailService.sendEmail("jamie.e.shaw@gmail.com", subject, message);
        
        setSuccessMessage(request, "Your message has been sent.");
       
        return "redirect:/home";
        
    }
    
}
