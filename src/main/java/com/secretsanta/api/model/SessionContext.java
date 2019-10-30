package com.secretsanta.api.model;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class SessionContext {
    
    private String schema;
    
    public void setSchema(String newSchema) {
        schema = newSchema;
    }
    
    public String getSchema() {
        return schema;
    }
    
    @PostConstruct
    public void sayHello() {
        System.out.println("hello");
    }
}
