package com.secretsanta.api.controller;

import java.time.Year;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;

@Controller
public class SantaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request, Model model) {
        request.getSession().setAttribute("CURRENT_YEAR", "2018");

        String SQL = "SELECT Recipient " +
                "FROM recipient " + 
               "WHERE UserName = ? AND Year = ?";
 
        String recipient = jdbcTemplate.queryForObject(SQL, new Object[]{"Jamie", "2017"}, String.class);
        request.getSession().setAttribute("RECIPIENT", recipient);
            
        return "home";
    }
    
    @GetMapping("/home")
    public String showHome(Model model) {
        return "home";
    }
    
    @GetMapping("/gift/summary")
    public String showGiftSummary(Model model) {
        
        String SQL = "SELECT GiftId, Description, Username, Year " + 
                     "  FROM gift " + 
                     " WHERE UserName = ? AND Year = ?";
        
        List<Gift> gifts = jdbcTemplate.query(
                SQL,
                new Object[]{"Jamie", "2017"},
                (rs, rowNum) ->
                        new Gift(
                                rs.getString("GiftId"),
                                rs.getString("UserName"),
                                rs.getString("Description"),
                                rs.getString("Year")
                        )
        );
        model.addAttribute("GIFTS", gifts);

        return "gift-summary";
    }
    
    @GetMapping("/gift/detail")
    public String showGiftDetail(@RequestParam String giftId, Model model) {
        
        if (giftId != null) {
            String SQL =  "SELECT GiftId, Description, Username, Year " +
                            "FROM gift " +
                           "WHERE GiftId = ?";
            
            List<Gift> gifts = jdbcTemplate.query(
                    SQL,
                    new Object[]{giftId},
                    (rs, rowNum) ->
                            new Gift(
                                    rs.getString("GiftId"),
                                    rs.getString("UserName"),
                                    rs.getString("Description"),
                                    rs.getString("Year")
                            )
            );
            model.addAttribute("GIFTS", gifts);
        }
        return "gift-detail";
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
    public String showPickStatus(Model model) {
        
        // Get the current year
        int currentYear = Year.now().getValue();
        
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
    
}
