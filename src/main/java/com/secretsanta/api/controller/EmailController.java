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

import com.secretsanta.api.mapper.UserMapper;
import com.secretsanta.api.model.User;

@Controller
@SessionAttributes({"CURRENT_YEAR", "CURRENT_USER", "RECIPIENT"})
public class EmailController extends BaseController {
    
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
               
        return "email";
        
//            emailMessage = Request.Form("message")
//
//            url = "http://www.pmshockey.com/wp-admin/admin-ajax.php?"
//
//            args = "action=send_email"
//            args = args + "&to=" + emailTo
//            args = args + "&message=" + emailMessage
//        
//            Set xmlHttp = Server.Createobject("MSXML2.ServerXMLHTTP.6.0")
//            xmlHttp.Open "GET", url + args, False
//
//            xmlHttp.setRequestHeader "content-type", "application/x-www-form-urlencoded"
    }
    
}
