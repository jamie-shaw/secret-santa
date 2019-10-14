package com.secretsanta.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.mapper.RecipientMapper;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

@Controller
@SessionAttributes({"CURRENT_YEAR", "CURRENT_USER", "RECIPIENT"})
public class SantaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
    @GetMapping({"/", "/home"})
    public String showHome() {
        return "home";
    }
    
    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }
    
    @GetMapping("/resetPassword")
    public String showResetPassword(Model model) {
        
        String SQL = "SELECT UserName " + 
                       "FROM user";
   
        List<User> users = jdbcTemplate.query(
                SQL,
                new Object[]{},
                (rs, rowNum) ->
                        new User(
                            rs.getString("UserName")
                        )
        );
        model.addAttribute("USERS", users);
        
        return "reset-password";
    }
    
    @PostMapping("/resetPassword")
    public String processResetPassword(@ModelAttribute("CURRENT_YEAR") Integer currentYear, HttpServletRequest request) {
        
        List<String> usernames = Arrays.asList(request.getParameterValues("username"));
        
        if (usernames.size() > 0) {
            String SQL = "UPDATE user " +
                            "SET user.Password = 'santa', user.PasswordExpired = true " +
                          "WHERE user.UserName IN ('" +  String.join("','", usernames) + "')";
                        
            jdbcTemplate.update(SQL);
        }
        
        return "home";
    }

    @GetMapping("/history/{selectedYear}")
    public String showHistory(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                              @PathVariable Integer selectedYear, 
                              Model model) {
                
        // Get all of the active years
        String SQL = "SELECT distinct Year " +
                       "FROM recipient " +
                      "WHERE Year <> ? " +
                   "ORDER BY Year DESC";
        
        List<String> years = jdbcTemplate.query(
                SQL, 
                new Object[] {currentYear},
                (rs, rowNum) -> new String(rs.getString("Year")));
        
        // Get all recipients for the selected year
        SQL = "SELECT UserName, Recipient, Year, Assigned " +
                "FROM recipient " +
               "WHERE Year = ? " +
            "ORDER BY UserName";
        
        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{selectedYear},new RecipientMapper());
       
        model.addAttribute("YEARS", years);
        model.addAttribute("RECIPIENTS", recipients);
        
        return "history";
    }
    
    @GetMapping("/pick/status")
    public String showPickStatus(@ModelAttribute("CURRENT_YEAR") Integer currentYear, Model model) {
        
        // Get all of the pickers
        String SQL = "SELECT UserName, Recipient, Year, Assigned " +
                       "FROM recipient " +
                 "INNER JOIN user ON recipient.UserName = user.UserName " +
                      "WHERE Year = ?";
        
        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{currentYear}, new RecipientMapper());
        
        model.addAttribute("PICKERS", recipients);
        
        return "pick-status";
    }
  
}
