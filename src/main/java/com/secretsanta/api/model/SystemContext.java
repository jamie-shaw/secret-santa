package com.secretsanta.api.model;

public class SystemContext {
    
    private String schema;
    private int year;
    
    public SystemContext(int year) {
        this.year = year;
    }

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
