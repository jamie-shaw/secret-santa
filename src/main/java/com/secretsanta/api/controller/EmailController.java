package com.secretsanta.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import com.secretsanta.api.mapper.UserMapper;
import com.secretsanta.api.model.User;

@Controller
@SessionAttributes({"CURRENT_YEAR", "CURRENT_USER", "RECIPIENT"})
public class EmailController extends BaseController {
    
    private static final String uri = "http://www.pmshockey.com/wp-admin/admin-ajax.php?";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/email")
    public String showEmail() {
        return "email";
    }
    
    @PostMapping("/email")
    public String processEmail(@ModelAttribute("CURRENT_YEAR") Integer currentYear,
                               @ModelAttribute("CURRENT_USER") String currentUser,
                               HttpServletRequest request,
                               Model model) {
        
        String filterColumn;
        
        if (request.getParameter("to").equals("recipient")) {
            // Get the info for the current user
            filterColumn = "user_name";
        } else {
            // Get the info for the current user's santa
            filterColumn = "recipient";
        }
            
        String SQL = "SELECT santa_user.user_name, display_name, email " +
                       "FROM " + getSchema() + ".recipient " + 
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.recipient = santa_user.user_name " +
                      "WHERE recipient." + filterColumn + " = ? AND Year = ?";

        User user = jdbcTemplate.queryForObject(SQL, new Object[]{currentUser, currentYear}, new UserMapper());
        
        String message = request.getParameter("message");

        RestTemplate restTemplate = new RestTemplate();
        
        restTemplate.getForEntity("http://www.pmshockey.com/wp-admin/admin-ajax.php?to=jamie.e.shaw@gmail.com&"
                                                                                 + "subject=My Subject&"
                                                                                 + "action=send_email&"
                                                                                 + "message=sage", String.class);
        
        return "email";
        
    }
    
}
