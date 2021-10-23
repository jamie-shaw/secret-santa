package com.secretsanta.api.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.model.PasswordChangeForm;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.User;
import com.secretsanta.api.service.PickService;

import io.micrometer.core.instrument.util.StringUtils;

@Controller
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class AdminController extends BaseController {
    
    @Autowired
    private RecipientDao recipientDao;

    @Autowired
    private UserDao userDao;
        
    @Autowired
    SystemDao systemDao;
    
    @Autowired
    private PickService pickService;
    
    @Autowired
    SessionContext sessionContext;
    
    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }
    
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
        
        return "admin";
    }
    
}
