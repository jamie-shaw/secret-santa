package com.secretsanta.api.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.context.Context;

import com.secretsanta.api.dao.GiftDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.EmailService;

@RestController
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
@RequestMapping("/api")
public class GiftRestController extends BaseController {
    
    @Resource
    private GiftDao dao;
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private EmailService emailService;
    
    @GetMapping("/gift/summary")
    public List<Gift> showIdeasForSanta(@ModelAttribute("CURRENT_USER") String currentUser,
                                  Model model) {
        
        return dao.getIdeasForSanta(currentUser);
    }
    
    @GetMapping("/idea/summary")
    public List<Gift> showIdeasFromSanta(@ModelAttribute("RECIPIENT") Recipient recipient) {
        return dao.getIdeasFromSanta(recipient.getRecipient());
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
                             @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.createGift(currentUser, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just gave you an idea";
        notifySanta(currentUser, gift, subject, "createGiftTemplate.html");
        
        return "redirect:/gift/summary";
    }
    
    @PostMapping("/gift/{giftId}/update")
    public String updateGift(@PathVariable int giftId, 
                             @ModelAttribute("giftForm") Gift gift,
                             @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.updateGift(giftId, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just updated an idea";
        notifySanta(currentUser, gift, subject, "updateGiftTemplate.html");
        
        return "redirect:/gift/summary";
    }
    
    @PostMapping("/gift/{giftId}/delete")
    public String deleteGift(@PathVariable int giftId,
                             @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // look up the gift
        Gift gift = dao.getGiftDetail(giftId);
        
        // delete the idea from the database
        dao.deleteGift(giftId);
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just deleted a suggestion";
        notifySanta(currentUser, gift, subject, "deleteGiftTemplate.html");
        
        return "redirect:/gift/summary";
    }
    
    private void notifySanta(String currentUser, Gift gift, String subject, String templateName) {
        
        // Get the info for the current user's santa
        User user = userDao.getUser(currentUser, FilterColumn.RECIPIENT);
        
        emailService.sendEmail(user.getEmail(), subject, templateName, new Context());
        
    }
}
