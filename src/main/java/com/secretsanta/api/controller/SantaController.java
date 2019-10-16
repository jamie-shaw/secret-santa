package com.secretsanta.api.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        // get the current year
        String SQL = "SELECT attribute_value " +
                       "FROM system ";
 
        String currentYear = jdbcTemplate.queryForObject(SQL, new Object[]{}, String.class);
        
        model.addAttribute("CURRENT_YEAR", currentYear);
        
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
    
    @GetMapping("/changePassword")
    public String showPasswordChange() {
        return "change-password";
    }
    
    @PostMapping("/pick")
    public String processPick(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                              @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // select all recipients that haven't already been assigned
        String SQL = "SELECT * " +
                       "FROM recipient " +
                      "WHERE Assigned = 'N' AND UserName <> ? AND Year = ?";

        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{currentUser, currentYear}, new RecipientMapper());
        Recipient recipient = null;
        
        if (recipients.size() > 2) {
            //Generate random value between 0 and number of records returned.
            int recordNumber = (int)(recipients.size() * Math.random());

            // Locate record corresponding to random number
            recipient = recipients.get(recordNumber);
            
        } else {
            // Choose the first unassigned user as the recipient
            recipient = recipients.get(0);
            
            if (recipients.size() == 2) {
                //Check to ensure that last user won't get themselves
                Recipient lastUser = recipients.get(1);
                if (lastUser.getRecipient() == null && !lastUser.isAssigned()) {
                    recipient = lastUser;
                }
            }
            
        }
        
        // Update recipient record
        SQL = "UPDATE recipient " + 
                 "SET Assigned = 'Y' " +
               "WHERE UserName = ? AND Year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), currentYear});

        // Update user record
        SQL = "UPDATE recipient " +
                 "SET Recipient = ? " +
               "WHERE UserName = ? AND Year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), currentUser, currentYear});
        
        return "redirect:/home";
    }
    
    
    @GetMapping({"/", "/home"})
    public String showHome(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                           @ModelAttribute("CURRENT_USER") String currentUser,
                           Model model) {
        
        // get the recipients for the current user
        String SQL = "SELECT * " +
                       "FROM recipient " +
                      "WHERE UserName = ? AND Year = ?";

        List<Recipient> recipients = jdbcTemplate.query(
                  SQL,
                  new Object[]{currentUser, currentYear},
                  (rs, rowNum) ->
                          new Recipient(
                                  rs.getString("UserName"),
                                  rs.getString("Year"),
                                  rs.getString("Recipient"),
                                  rs.getString("Assigned").equals("Y")
                  )
          );
          
        model.addAttribute("RECIPIENT", recipients.get(0));
        
        // get the days until Christmas
        LocalDate christmas = LocalDate.of(currentYear, Month.DECEMBER, 25);
        LocalDate now = LocalDate.now();
        
        long daysUntil = ChronoUnit.DAYS.between(now, christmas);
        
        model.addAttribute("DAYS_UNTIL", daysUntil);
        
        return "home";
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
                    "SET user.Password = ?, user.PasswordExpired = true " +
                  "WHERE user.UserName = ?";
            
            for (String username : usernames) {
                String password = passwordEncoder.encode("santa");
                jdbcTemplate.update(SQL, new Object[] {password, username});
            }
        
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
