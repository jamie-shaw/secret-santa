package com.secretsanta.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SystemDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public int getCurrentYear() {
        String SQL = "SELECT attribute_value " +
                       "FROM system.system ";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{}, Integer.class);
    }
    
    
    public void setCurrentYear(int year) {
        String SQL = "UPDATE system.system " +
                     "   SET attribute_value = ?";
        
        jdbcTemplate.update(SQL, new Object[] {year});
    }

}
