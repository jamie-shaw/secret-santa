package com.secretsanta.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.GiftDao;
import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class GiftController extends BaseController {
    
    @Autowired
    private GiftDao dao;
    
    @GetMapping("/gift/summary")
    public String showIdeasForSanta(@ModelAttribute("CURRENT_USER") String currentUser,
                                  Model model) {
        
        List<Gift> gifts = dao.getIdeasForSanta(currentUser);
        model.addAttribute("GIFTS", gifts);

        return "gift-summary";
    }
    
    @GetMapping("/idea/summary")
    public String showIdeasFromSanta(@ModelAttribute("RECIPIENT") Recipient recipient,
                                     Model model) {
        
        List<Gift> ideas = dao.getIdeasFromSanta(recipient.getRecipient());
        model.addAttribute("IDEAS", ideas);
        
        return "ideas";
    }
    
    @GetMapping("/gift/{giftId}")
    public String showGiftDetail(@PathVariable int giftId, Model model) {
        
        Gift gift = dao.getGiftDetail(giftId);
        model.addAttribute("GIFT", gift);

        return "gift-detail";
    }
    
    @GetMapping("/gift")
    public String showCreateGift() {
        return "gift-detail";
    }
    
    @PostMapping("/gift")
    public String createGift(@ModelAttribute("giftForm") Gift gift,
                             @ModelAttribute("CURRENT_USER") String currentUser,
                             Model model) {
        
        dao.createGift(currentUser, gift.getDescription());

        return "redirect:/gift/summary";
    }
    
    @PostMapping("/gift/{giftId}/update")
    public String updateGift(@PathVariable int giftId, @ModelAttribute("giftForm") Gift gift) {
        
        dao.updateGift(giftId, gift.getDescription());
        
        return "redirect:/gift/summary";
    }
    
    @PostMapping("/gift/{giftId}/delete")
    public String deleteGift(@PathVariable int giftId) {
        
        dao.deleteGift(giftId);

        return "redirect:/gift/summary";
    }
}
