package com.secretsanta.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
@SessionAttributes({"CURRENT_USER", "RECIPIENT"})
public class EmailController extends BaseController {
    
    private static final String uri = "http://www.pmshockey.com/wp-admin/admin-ajax.php?";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/email")
    public String showEmail() {
        return "email";
    }
    
    @PostMapping("/email")
    public String processEmail(@ModelAttribute("CURRENT_USER") String currentUser,
                               HttpServletRequest request,
                               Model model) {
        
        String filterColumn;
        String subject;
        
        if (request.getParameter("to").equals("recipient")) {
            // Get the info for the current user
            filterColumn = "user_name";
            subject = "A message from your Secret Santa";
        } else {
            // Get the info for the current user's santa
            filterColumn = "recipient";
            subject = "A message from your Secret Santa recipient";
        }
            
        String SQL = "SELECT santa_user.user_name, display_name, email " +
                       "FROM " + getSchema() + ".recipient " + 
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.recipient = santa_user.user_name " +
                      "WHERE recipient." + filterColumn + " = ? AND Year = ?";

        User user = jdbcTemplate.queryForObject(SQL, new Object[]{currentUser, getCurrentYear()}, new UserMapper());
        
        String message = request.getParameter("message");

        RestTemplate restTemplate = new RestTemplate();
        
        restTemplate.getForEntity(uri + "to=jamie.e.shaw@gmail.com&"
                                      + "subject=" + subject + "&"
                                      + "message=" + message + "&"
                                      + "action=send_email&", String.class);
        
       setSuccessMessage(request, "Your message has been sent.");
       
        return "redirect:/email";
        
    }
    
}
