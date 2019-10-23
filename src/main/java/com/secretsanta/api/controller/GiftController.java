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

import com.secretsanta.api.mapper.GiftMapper;
import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT", "CURRENT_YEAR"})
public class GiftController extends BaseController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/gift/summary")
    public String showGiftSummary(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                                  @ModelAttribute("CURRENT_USER") String currentUser,
                                  Model model) {
        
        String SQL = "SELECT gift_id, description, user_name, year " + 
                     "  FROM " + getSchema() + ".gift " + 
                     " WHERE user_name = ? AND year = ?";
        
        List<Gift> gifts = jdbcTemplate.query(SQL, new Object[]{currentUser, currentYear}, new GiftMapper());

        model.addAttribute("GIFTS", gifts);

        return "gift-summary";
    }
    
    @GetMapping("/gift/{giftId}")
    public String showGiftDetail(@PathVariable String giftId, Model model) {
        
        String SQL =  "SELECT gift_id, description, user_name, year " +
                        "FROM" + getSchema() + ".gift " +
                       "WHERE gift_id = ?";
        
        List<Gift> gifts = jdbcTemplate.query(SQL, new Object[]{giftId}, new GiftMapper());
        
        model.addAttribute("GIFT", gifts.get(0));

        return "gift-detail";
    }
    
    @PostMapping("/gift/{giftId}/update")
    public String updateGift(@PathVariable String giftId, @ModelAttribute("giftForm") Gift gift) {
        
        String SQL = "UPDATE " + getSchema() + ".gift " + 
                        "SET description = ? " +
                      "WHERE gift_id = ?";
        
        jdbcTemplate.update(SQL, new Object[]{gift.getDescription(), giftId});

        return "redirect:/gift/summary";
    }
    
    @PostMapping("/gift/{giftId}/delete")
    public String deleteGift(@PathVariable int giftId) {
        
        String SQL =  "DELETE " +
                        "FROM " + getSchema() + ".gift " +
                       "WHERE gift_id = ?";
        
        jdbcTemplate.update(SQL, new Object[]{giftId});

        return "redirect:/gift/summary";
    }
    
    @GetMapping("/gift")
    public String showCreateGift() {
        return "gift-detail";
    }
    
    @PostMapping("/gift")
    public String createGift(@ModelAttribute("giftForm") Gift gift,
                             @ModelAttribute("CURRENT_YEAR") Integer currentYear,
                             @ModelAttribute("CURRENT_USER") String currentUser,
                             Model model) {
        
        String SQL =  "INSERT INTO " + getSchema() + ".gift " +
                      "VALUES(DEFAULT, ?, ?, ?)";
        
        jdbcTemplate.update(SQL, new Object[]{currentUser, gift.getDescription(), currentYear});

        return "redirect:/gift/summary";
    }
    
    @GetMapping("/idea/summary")
    public String showIdeas(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                            @ModelAttribute("RECIPIENT") Recipient recipient,
                            Model model) {
        
        String SQL = "SELECT gift_id, user_name, description, year " +
                       "FROM " + getSchema() + ".gift " +
                      "WHERE user_name = ? AND year = ?";
        
        List<Gift> ideas = jdbcTemplate.query(SQL, new Object[]{recipient.getRecipient(), currentYear}, new GiftMapper());

        model.addAttribute("IDEAS", ideas);
        
        return "ideas";
    }

}
