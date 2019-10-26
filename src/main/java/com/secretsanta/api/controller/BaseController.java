package com.secretsanta.api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.secretsanta.api.model.SystemContext;

public abstract class BaseController {

    @Autowired
    SystemContext systemContext;
    
    public String getSchema() {
        return systemContext.getSchema();
    }
    
    public int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
}
