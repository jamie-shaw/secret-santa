package com.secretsanta.api.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.mapper.RecipientMapper;
import com.secretsanta.api.mapper.UserMapper;
import com.secretsanta.api.model.PasswordChangeForm;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class SantaController extends BaseController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationProvider authenticationProvider;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";
    }
    
    @GetMapping("/login/error")
    public String showLoginError(Model model) {
        
        model.addAttribute("ERROR", true);
        
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
    
    @PostMapping("/changePassword")
    public String processPasswordChange(HttpServletRequest request, @ModelAttribute("form") PasswordChangeForm form, Model model) {
        
        String username = (String)request.getSession().getAttribute("username");
        String password = form.getPassword();
        
        if (!password.equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords must match");
        }
        
        // Update the password
        String SQL = "UPDATE " + getSchema() + ".santa_user " + 
                        "SET password = ?, "  +
                           " password_expired = 'false' " +
                      "WHERE user_name = ?";
        
        jdbcTemplate.update(SQL, new Object[] {passwordEncoder.encode(password), username});
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        
        authenticationProvider.authenticate(authentication);
              
        return "redirect:/home";
    }
    
    @PostMapping("/pick")
    public String processPick(@ModelAttribute("CURRENT_USER") String currentUser) {
        
        // select all recipients that haven't already been assigned
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE assigned = 'N' AND user_name <> ? AND year = ?";
        
        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{currentUser, getCurrentYear()}, new RecipientMapper());
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
        SQL = "UPDATE " + getSchema() + ".recipient " + 
                 "SET assigned = 'Y' " +
               "WHERE user_name = ? AND year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), getCurrentYear()});
        
        // Update user record
        SQL = "UPDATE " + getSchema() + ".recipient " +
                 "SET recipient = ? " +
               "WHERE user_name = ? AND year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), currentUser, getCurrentYear()});
        
        return "redirect:/home";
    }
    
    @GetMapping({"/", "/home"})
    public String showHome(@ModelAttribute("CURRENT_USER") String currentUser,
                           Model model) {
        
        // get the recipient for the current user
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE user_name = ? AND year = ?";
        
        Recipient recipient = jdbcTemplate.queryForObject(SQL, new Object[]{currentUser, getCurrentYear()}, new RecipientMapper());
        model.addAttribute("RECIPIENT", recipient);
        
        // get the days until Christmas
        LocalDate christmas = LocalDate.of(getCurrentYear(), Month.DECEMBER, 25);
        LocalDate now = LocalDate.now();
        
        long daysUntil = ChronoUnit.DAYS.between(now, christmas);
        
        model.addAttribute("DAYS_UNTIL", daysUntil);
        
        return "home";
    }
    
    @GetMapping("/resetPassword")
    public String showResetPassword(Model model) {
        
        String SQL = "SELECT user_name, display_name, email " + 
                       "FROM " + getSchema() + ".santa_user";
        
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
        model.addAttribute("USERS", users);
        
        return "reset-password";
    }
    
    @PostMapping("/resetPassword")
    public String processResetPassword(HttpServletRequest request) {
        
        List<String> usernames = Arrays.asList(request.getParameterValues("username"));
        
        if (usernames.size() > 0) {
            String SQL = "UPDATE " + getSchema() + ".santa_user " +
                            "SET password = ?, password_expired = true " +
                          "WHERE user_name = ?";
            
            for (String username : usernames) {
                String password = passwordEncoder.encode("santa");
                jdbcTemplate.update(SQL, new Object[] {password, username});
            }
        
        }
        
        return "home";
    }

    @GetMapping("/history/{selectedYear}")
    public String showHistory(@PathVariable Integer selectedYear, 
                              Model model) {
                
        // Get all of the active years
        String SQL = "SELECT distinct year " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year <> ? " +
                   "ORDER BY year DESC";
        
        List<String> years = jdbcTemplate.query(
                SQL, 
                new Object[] {getCurrentYear()},
                (rs, rowNum) -> new String(rs.getString("year")));
        
        // Get all recipients for the selected year
        SQL = "SELECT user_name, recipient, year, assigned " +
                "FROM " + getSchema() + ".recipient " +
               "WHERE year = ? " +
            "ORDER BY user_name";
        
        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{selectedYear},new RecipientMapper());
       
        model.addAttribute("YEARS", years);
        model.addAttribute("RECIPIENTS", recipients);
        
        return "history";
    }
    
    @GetMapping("/pick/status")
    public String showPickStatus(Model model) {
        
        // Get all of the pickers
        String SQL = "SELECT recipient.user_name, recipient, year, assigned " +
                       "FROM " + getSchema() + ".recipient " +
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.user_name = santa_user.user_name " +
                      "WHERE year = ?";
        
        List<Recipient> recipients = jdbcTemplate.query(SQL, new Object[]{getCurrentYear()}, new RecipientMapper());
        
        model.addAttribute("PICKERS", recipients);
        
        return "pick-status";
    }
  
}
