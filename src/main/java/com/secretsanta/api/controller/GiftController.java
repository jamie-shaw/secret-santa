package com.secretsanta.api.controller;

import java.util.List;

import jakarta.annotation.Resource;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
@RequestMapping("/api")
@Tag(name = "Gifts", description = "Endpoints for managing gift ideas and suggestions")
@SecurityRequirement(name = "bearerAuth")
public class GiftController {
    
    @Resource
    private GiftDao dao;
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private EmailService emailService;
    
    @Operation(
        summary = "Get gift ideas for Santa",
        description = "Retrieves all gift ideas from the current user's Secret Santa recipient"
    )
    @GetMapping("/gift/summary")
    public List<Gift> getIdeasForSanta(@ModelAttribute("CURRENT_USER") String currentUser,
                                  Model model) {
        
        return dao.getIdeasForSanta(currentUser);
    }
    
    @Operation(
        summary = "Get user's own gift ideas",
        description = "Retrieves all gift ideas that the current user has submitted"
    )
    @GetMapping("/idea/summary")
    public List<Gift> getIdeasFromRecipient(@ModelAttribute("RECIPIENT") Recipient recipient) {
        return dao.getIdeasFromSanta(recipient.getRecipient());
    }
    
    @Operation(
        summary = "Get gift details",
        description = "Retrieves detailed information about a specific gift idea"
    )
    @GetMapping("/gift/{giftId}")
    public Gift showGiftDetail(
            @Parameter(description = "ID of the gift to retrieve", required = true, example = "1")
            @PathVariable int giftId, Model model) {
        return dao.getGiftDetail(giftId);
    }
    
    @Operation(
        summary = "Create gift idea form",
        description = "Returns the form view for creating a new gift idea"
    )
    @GetMapping("/gift")
    public String showCreateGift() {
        return "gift-detail";
    }
    
    @Operation(
        summary = "Create new gift idea",
        description = "Creates a new gift idea and sends notification email to the user's Secret Santa"
    )
    @PostMapping("/gift")
    public void createGift(
            @Parameter(description = "Gift idea details", required = true)
            @RequestBody Gift gift,
            @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.createGift(currentUser, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just gave you an idea";
        notifySanta(currentUser, gift, subject, "createGiftTemplate.html");
    }
    
    @Operation(
        summary = "Update gift idea",
        description = "Updates an existing gift idea and sends notification email to the user's Secret Santa"
    )
    @PostMapping("/gift/{giftId}")
    public void updateGift(
            @Parameter(description = "ID of the gift to update", required = true, example = "1")
            @PathVariable int giftId, 
            @Parameter(description = "Updated gift idea details", required = true)
            @RequestBody Gift gift,
            @ModelAttribute("CURRENT_USER") String currentUser) {
        
        // store the idea
        dao.updateGift(giftId, gift.getDescription(), gift.getLink());
        
        // and send the santa an email
        String subject = "Your Secret Santa recipient just updated an idea";
        notifySanta(currentUser, gift, subject, "updateGiftTemplate.html");;
    }
    
    @Operation(
        summary = "Delete gift idea",
        description = "Deletes a gift idea and sends notification email to the user's Secret Santa"
    )
    @DeleteMapping("/gift/{giftId}")
    public void deleteGift(
            @Parameter(description = "ID of the gift to delete", required = true, example = "1")
            @PathVariable int giftId,
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
