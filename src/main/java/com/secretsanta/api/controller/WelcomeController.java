package com.secretsanta.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.secretsanta.api.model.Gift;

@Controller
public class WelcomeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("year", "2018");

        String SQL = "SELECT * FROM USER";
            
        String recipient = jdbcTemplate.queryForObject(SQL, String.class);
        
        return "login";
    }
    
    @PostMapping("/login/process")
    public String processLogin(Model model) {
        
        String SQL = "SELECT Recipient " +
                       "FROM recipient " + 
                      "WHERE UserName = ? AND Year = ?";
        
        String recipient = jdbcTemplate.queryForObject(SQL, new Object[]{"Jamie", "2017"}, String.class);
        model.addAttribute("RECIPIENT", recipient);

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



}
