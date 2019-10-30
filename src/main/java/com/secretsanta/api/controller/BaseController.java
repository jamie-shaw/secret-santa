package com.secretsanta.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

public abstract class BaseController {

    static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";
    static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    
    @Autowired
    SystemContext systemContext;
    
    @Autowired
    private SessionContext sessionContext;
    
    public String getSchema() {
        return sessionContext.getSchema();
    }
    
    public int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
    
    void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SUCCESS_MESSAGE, message);
    }
    
    void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute(ERROR_MESSAGE, message);
    }

}
