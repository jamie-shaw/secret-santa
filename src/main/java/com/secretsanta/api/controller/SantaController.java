package com.secretsanta.api.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.model.PasswordChangeForm;
import com.secretsanta.api.model.User;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SantaController {
    
    static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";
    static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    
    @Resource
    private RecipientDao recipientDao;

    @Resource
    private UserDao userDao;
        
    @Resource
    SystemDao systemDao;
    
    @Resource
    private AuthenticationProvider authenticationProvider;
    
    @PostMapping("/changePassword")
    public String processPasswordChange(HttpServletRequest request, PasswordChangeForm form, Model model) {
        
        String password = form.getPassword();
        
        if (StringUtils.isBlank(password)) {
            //setErrorMessage(request, "Password must be entered.");
            return "redirect:/changePassword";
        }
        
        if (!password.equals(form.getConfirmPassword())) {
            //setErrorMessage(request, "Passwords must match.");
            return "redirect:/changePassword";
        }
        
        if (password.equals("santa")) {
            //setErrorMessage(request, "Password can't be 'santa'.");
            return "redirect:/changePassword";
        }
        
        User user = userDao.getUser((String)request.getSession().getAttribute("username"));
        String username = user.getUsername();
        
        // change the password
        userDao.changePassword(username, password);
        
        // and update the authentication
        Authentication initialAuthentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication processedAuthentication = authenticationProvider.authenticate(initialAuthentication);
        
        SecurityContextHolder.getContext().setAuthentication(processedAuthentication);
        
        return "redirect:/home";
    }
    
    @GetMapping("/resetPassword")
    public String showResetPassword(Model model) {
        
        List<User> users = userDao.getAllUsers();
        
        model.addAttribute("USERS", users);
        
        return "reset-password";
    }
    
    @PostMapping("/resetPassword")
    public String processResetPassword(HttpServletRequest request) {
        
        List<String> usernames = Arrays.asList(request.getParameterValues("username"));
        
        userDao.resetPasswords(usernames);
        
        return "home";
    }
}
