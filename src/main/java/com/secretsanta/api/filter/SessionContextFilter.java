package com.secretsanta.api.filter;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.secretsanta.api.model.SessionContext;

@Component
public class SessionContextFilter extends OncePerRequestFilter {

    @Resource
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
