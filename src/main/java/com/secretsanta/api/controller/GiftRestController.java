package com.secretsanta.api.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public List<Gift> getIdeasForSanta(@ModelAttribute("CURRENT_USER") String currentUser,
                                  Model model) {
        
        return dao.getIdeasForSanta(currentUser);
    }
    
    @GetMapping("/idea/summary")
    public List<Gift> getIdeasFromRecipient(@ModelAttribute("RECIPIENT") Recipient recipient) {
        return dao.getIdeasFromSanta(recipient.getRecipient());
    }
    
    @GetMapping("/gift/{giftId}")
    public Gift showGiftDetail(@PathVariable int giftId, Model model) {
        return dao.getGiftDetail(giftId);
    }
    
    @GetMapping("/gift")
    public String showCreateGift() {
        return "gift-detail";
    }
    
    @PostMapping("/gift")
    public void createGift(@RequestBody Gift gift,
                           @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.createGift(currentUser, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just gave you an idea";
        notifySanta(currentUser, gift, subject, "createGiftTemplate.html");
    }
    
    @PostMapping("/gift/{giftId}")
    public void updateGift(@PathVariable int giftId, 
                           @RequestBody Gift gift,
                           @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.updateGift(giftId, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just updated an idea";
        notifySanta(currentUser, gift, subject, "updateGiftTemplate.html");;
    }
    
    @DeleteMapping("/gift/{giftId}")
    public void deleteGift(@PathVariable int giftId,
                           @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // look up the gift
        Gift gift = dao.getGiftDetail(giftId);
        
        // delete the idea from the database
        dao.deleteGift(giftId);
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just deleted a suggestion";
        notifySanta(currentUser, gift, subject, "deleteGiftTemplate.html");
    }
    
    private void notifySanta(String currentUser, Gift gift, String subject, String templateName) {
        
        // Get the info for the current user's santa
        User user = userDao.getUser(currentUser, FilterColumn.RECIPIENT);
        
        emailService.sendEmail(user.getEmail(), subject, templateName, new Context());
        
    }
}
