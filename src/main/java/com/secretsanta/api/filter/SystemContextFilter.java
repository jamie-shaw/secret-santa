package com.secretsanta.api.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.model.SystemContext;
import com.secretsanta.api.util.SystemContextHolder;

@Component
public class SystemContextFilter extends OncePerRequestFilter {
    
    private static final String SYSTEM_CONTEXT = "SYSTEM_CONTEXT";
    
    private SystemDao systemDao;
    
    public SystemContextFilter(SystemDao systemDao) {
        this.systemDao = systemDao;
    }
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        SystemContext context = (SystemContext)request.getSession().getAttribute(SYSTEM_CONTEXT);
        
        if (context == null) {
            int year = systemDao.getCurrentYear();
            context = new SystemContext(year);
        }
        
        String schema = request.getParameter("edition");
        if (schema != null) {
            context.setSchema(schema);
        }
        
        SystemContextHolder.setSystemContext(context);
        request.getSession().setAttribute(SYSTEM_CONTEXT, context);
        
        doFilter(request, response, filterChain);
        
        SystemContextHolder.resetSystemContext();
    }

}
