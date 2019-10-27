package com.secretsanta.api.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

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
    
    public long getDaysUntilChristmas() {
        LocalDate christmas = LocalDate.of(getCurrentYear(), Month.DECEMBER, 25);
        LocalDate now = LocalDate.now();
        
        return ChronoUnit.DAYS.between(now, christmas);
    }

}
