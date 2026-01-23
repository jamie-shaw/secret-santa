package com.secretsanta.api.controller;

import java.util.Arrays;
import java.util.Calendar;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;
import com.secretsanta.api.service.PickService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class AdminController {
    
    static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";
    static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    
    @Resource
    private RecipientDao recipientDao;
    
    @Resource
    private UserDao userDao;
        
    @Resource
    SystemDao systemDao;
    
    @Resource
    private PickService pickService;
    
    @Resource
    SystemContext systemContext;
    
    @Resource
    private SessionContext sessionContext;
    
    @GetMapping("/rollOver")
    public String rollSantaOver(HttpServletRequest request) {
        
        int year = Calendar.getInstance().get(Calendar.YEAR);
        
        // update the year
        systemDao.setCurrentYear(year);
        systemContext.setCurrentYear(year);
        
        String originalSchema = sessionContext.getSchema();
        
        for (String schema : Arrays.asList(new String[] {"shaw", "fernald"})) {
            // change the active schema
            sessionContext.setSchema(schema);
            
            // reset the passwords for all users
            userDao.resetAllPasswords();
            
            // reset the recipients for all users
            recipientDao.resetAllRecipients();
            
            // pick all recipients
            while (!pickService.pickRecipients()) {
                // loop until pick recipients doesn't fail
            }
        }
        
        // return to the original schema
        sessionContext.setSchema(originalSchema);
        
        setSuccessMessage(request, "Rollover complete.");
        
        log.debug("Rollover complete.");
        
        return "admin";
    }
    void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SUCCESS_MESSAGE, message);
    }
    
    void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute(ERROR_MESSAGE, message);
    }
    
    boolean messagesExist(HttpServletRequest request) {
        return null != request.getSession().getAttribute(ERROR_MESSAGE);
    }
}
