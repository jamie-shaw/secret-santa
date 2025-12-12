package com.secretsanta.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.model.Recipient;

@RestController
@SessionAttributes({"CURRENT_USER"})
@RequestMapping("/api")
public class RecipientRestController extends BaseController {
    
    @Resource
    private RecipientDao recipientDao;
    
    @GetMapping("/recipient")
    public Recipient getRecipient(@ModelAttribute("CURRENT_USER") String currentUser) {
        return recipientDao.getRecipientForCurrentUser(currentUser);
    }
}
