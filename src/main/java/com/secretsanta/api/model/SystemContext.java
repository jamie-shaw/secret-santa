package com.secretsanta.api.model;

import org.springframework.stereotype.Component;

@Component
public class SystemContext {
    
    private String schema;
    private int year;
    
    public void setSchema(String newSchema) {
        schema = newSchema;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setCurrentYear(int newYear) {
        year = newYear;
    }
    
    public int getCurrentYear() {
        return year;
    }
    
}
