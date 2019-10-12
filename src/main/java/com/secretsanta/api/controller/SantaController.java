package com.secretsanta.api.controller;

import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

@Controller
@SessionAttributes({"USER", "RECIPIENT", "CURRENT_YEAR"})
public class SantaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/login")
    public String showLogin(Model model) {

        String SQL = "SELECT attribute_value " +
                       "FROM system ";
        
        String year = jdbcTemplate.queryForObject(SQL, new Object[]{}, String.class);
        
        SQL = "SELECT Recipient " +
                "FROM recipient " + 
               "WHERE UserName = ? AND Year = ?";

        String recipient = jdbcTemplate.queryForObject(SQL, new Object[]{"Jamie", "2017"}, String.class);
        
        model.addAttribute("RECIPIENT", recipient);
        model.addAttribute("CURRENT_YEAR", year);
        model.addAttribute("USER", "Jamie");
        
        return "home";
    }
    
    @GetMapping("/home")
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

    @GetMapping("history")
    public String showHistory(@RequestParam Integer selectedYear, Model model) {
        
        // Get the current year
        int currentYear = Year.now().getValue();
        
        // Get all of the active years
        String SQL = "SELECT distinct Year " +
                       "FROM recipient " +
                      "WHERE Year <> ? " +
                   "ORDER BY Year DESC";
        
        List<String> years = jdbcTemplate.query(
                SQL, 
                new Object[] {currentYear},
                (rs, rowNum) -> new String(rs.getString("Year")));
        
        if (selectedYear == null) {
            selectedYear = currentYear;
        }

        // Get all recipients for the selected year
        SQL = "SELECT UserName, Recipient, Year, Assigned " +
                "FROM recipient " +
               "WHERE Year = ? " +
            "ORDER BY UserName";
        
        List<Recipient> recipients = jdbcTemplate.query(
                SQL,
                new Object[]{selectedYear},
                (rs, rowNum) ->
                        new Recipient(
                                rs.getString("UserName"),
                                rs.getString("Year"),
                                rs.getString("Recipient"),
                                rs.getString("Assigned").equals("Y")
                )
        );
       
        model.addAttribute("CURRENT_YEAR", currentYear);
        model.addAttribute("YEARS", years);
        model.addAttribute("RECIPIENTS", recipients);
        
        return "history";
    }
    
    @GetMapping("pick/status")
    public String showPickStatus(@ModelAttribute("CURRENT_YEAR") Integer currentYear, Model model) {
        
        // Get all of the pickers
        String SQL = "SELECT UserName, Recipient, Year, Assigned " +
                       "FROM recipient " +
                 "INNER JOIN user ON recipient.UserName = user.UserName " +
                      "WHERE Year = ?";
        
        List<Recipient> recipients = jdbcTemplate.query(
                SQL,
                new Object[]{currentYear},
                (rs, rowNum) ->
                        new Recipient(
                                rs.getString("UserName"),
                                rs.getString("Year"),
                                rs.getString("Recipient"),
                                rs.getString("Assigned").equals("Y")
                )
        );
        
        model.addAttribute("PICKERS", recipients);
        
        return "pick-status";
    }
    
    @GetMapping("idea/summary")
    public String showIdeas(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                            @ModelAttribute("RECIPIENT") String recipient,
                            Model model) {
        
        String SQL = "SELECT GiftId, Description " +
                       "FROM gift " +
                      "WHERE UserName = ? AND Year = ?";
        
        List<Gift> ideas = jdbcTemplate.query(
                SQL,
                new Object[]{recipient, currentYear},
                (rs, rowNum) ->
                        new Gift(
                                rs.getString("GiftId"),
                                rs.getString("UserName"),
                                rs.getString("Description"),
                                rs.getString("Year")
                )
        );
        model.addAttribute("RECIPIENT", recipient);
        model.addAttribute("IDEAS", ideas);
        
        return "ideas";
    }
    
}
