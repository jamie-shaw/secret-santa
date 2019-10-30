package com.secretsanta.api.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.secretsanta.api.model.SessionContext;

@Component
public class SessionContextFilter extends OncePerRequestFilter {

    @Autowired
    private SessionContext sessionContext;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String edition = request.getParameter("edition");
        
        if (edition != null && !edition.contentEquals("")) {
            sessionContext.setSchema(edition);
        }
        
        chain.doFilter(request, response);
    }
    
}
