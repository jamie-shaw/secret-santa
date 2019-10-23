package com.secretsanta.api.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException  {
    
        // get the current user
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
         
        request.getSession().setAttribute("CURRENT_USER", currentUser);
        
        response.sendRedirect("/home");
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        
        // store the username in the session for the change password process
        String username = request.getParameter("username");
        request.getSession().setAttribute("username", username);
        
        if (exception instanceof CredentialsExpiredException) {
            response.sendRedirect("/changePassword");
        } else {
            response.sendRedirect("/login/error");
        }
    }
}