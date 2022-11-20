package com.secretsanta.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

public abstract class BaseController {

    static final String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";
    static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    
    @Resource
    SystemContext systemContext;
    
    @Resource
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
    
    boolean messagesExist(HttpServletRequest request) {
        return null != request.getSession().getAttribute(ERROR_MESSAGE);
    }

}
