package com.secretsanta.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.model.Gift;

@Controller
@SessionAttributes({"USER", "RECIPIENT", "CURRENT_YEAR"})
public class GiftController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/gift/summary")
    public String showGiftSummary(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                                  @ModelAttribute("USER") String user,
                                  Model model) {
        
        String SQL = "SELECT GiftId, Description, Username, Year " + 
                     "  FROM gift " + 
                     " WHERE UserName = ? AND Year = ?";
        
        List<Gift> gifts = jdbcTemplate.query(
                SQL,
                new Object[]{user, currentYear},
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
    
    @GetMapping("/gift/view/{giftId}")
    public String showGiftDetail(@PathVariable String giftId, Model model) {
        
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

        return "gift-detail";
    }
    
    @GetMapping("/gift/create")
    public String createGift() {
        return "gift-detail";
    }
    
    @PostMapping("/gift/create")
    public String createGift(@ModelAttribute("giftForm") Gift gift,
                             @ModelAttribute("CURRENT_YEAR") Integer currentYear,
                             @ModelAttribute("USER") String user,
                             Model model) {
        
        String SQL =  "INSERT INTO gift " +
                      "VALUES(?, ?, ?, ?)";
        
        jdbcTemplate.update(
                SQL,
                new Object[]{gift.getId(), user, gift.getDescription(), currentYear}
        );

        return showGiftSummary(currentYear, user, model);
    }

}
