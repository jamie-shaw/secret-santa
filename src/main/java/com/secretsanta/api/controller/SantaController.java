package com.secretsanta.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.model.PasswordChangeForm;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.PickService;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class SantaController extends BaseController {
    
    @Autowired
    private RecipientDao recipientDao;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private AuthenticationProvider authenticationProvider;
    
    @Autowired
    private PickService pickService;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";
    }
    
    @GetMapping("/login/error")
    public String showLoginError(HttpServletRequest request) {
        
        setErrorMessage(request, "Your login failed.  Please check your username and password and try again.");
        
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "login";
    }
    
    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }
    
    @GetMapping("/rollOver")
    public String rollSantaOver(HttpServletRequest request) {
        
        while (!pickService.pickRecipients()) {
        }
        
        setSuccessMessage(request, "Rollover complete.");
        
        return "admin";
    }
    
    @GetMapping("/changePassword")
    public String showPasswordChange(HttpServletRequest request) {
        
        setErrorMessage(request, "Please enter a new password");
        
        return "change-password";
    }
    
    @PostMapping("/changePassword")
    public String processPasswordChange(HttpServletRequest request, @ModelAttribute("form") PasswordChangeForm form, Model model) {
        
        String username = (String)request.getSession().getAttribute("username");
        String password = form.getPassword();
        
        if (!password.equals(form.getConfirmPassword())) {
            setErrorMessage(request, "Passwords must match");
            return "redirect:/changePassword";
        }
        
        // change the password
        userDao.changePassword(username, password);
        
        // and update the authentication
        Authentication initialAuthentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication processedAuthentication = authenticationProvider.authenticate(initialAuthentication);
        
        SecurityContextHolder.getContext().setAuthentication(processedAuthentication);
        
        request.getSession().setAttribute("CURRENT_USER", username);
        
        return "redirect:/home";
    }
    
    @PostMapping("/pick")
    public String processPick(@ModelAttribute("CURRENT_USER") String currentUser) {
        
        recipientDao.processPick(currentUser);
        
        return "redirect:/home";
    }
    
    @GetMapping({"/", "/home"})
    public String showHome(@ModelAttribute("CURRENT_USER") String currentUser,
                           Model model) {
        
        Recipient recipient = recipientDao.getRecipientForCurrentUser(currentUser);
        model.addAttribute("RECIPIENT", recipient);
        
        return "home";
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

    @GetMapping("/history/{selectedYear}")
    public String showHistory(@PathVariable Integer selectedYear, Model model) {
        
        List<String> years = recipientDao.getActiveYears();
        
        List<Recipient> recipients = recipientDao.getAllRecipientsForSelectedYear(selectedYear);
        
        model.addAttribute("YEARS", years);
        model.addAttribute("RECIPIENTS", recipients);
        model.addAttribute("SELECTED_YEAR", selectedYear);
        
        return "history";
    }
    
    @GetMapping("/pick/status")
    public String showPickStatus(Model model) {
        
        // Get all of the pickers
        List<Recipient> recipients = recipientDao.getAllRecipients();
        model.addAttribute("PICKERS", recipients);
        
        return "pick-status";
    }
  
}
