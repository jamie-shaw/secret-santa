package com.secretsanta.api.security;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.secretsanta.api.model.Recipient;

@Component
public class SecurityHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException  {
    
        // get the current user
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
         
        request.getSession().setAttribute("CURRENT_USER", currentUser);
        
        // get the current year
        String SQL = "SELECT attribute_value " +
                       "FROM system ";
 
        String currentYear = jdbcTemplate.queryForObject(SQL, new Object[]{}, String.class);
        
        request.getSession().setAttribute("CURRENT_YEAR", currentYear);

        // get the recipients for the current user
        SQL = "SELECT * " +
                "FROM recipient " + 
               "WHERE UserName = ? AND Year = ?";

        List<Recipient> recipients = jdbcTemplate.query(
                  SQL,
                  new Object[]{currentUser, currentYear},
                  (rs, rowNum) ->
                          new Recipient(
                                  rs.getString("UserName"),
                                  rs.getString("Year"),
                                  rs.getString("Recipient"),
                                  rs.getString("Assigned").equals("Y")
                  )
          );
          
        request.getSession().setAttribute("RECIPIENT", recipients.get(0));
        
        response.sendRedirect("/home");
    }
}