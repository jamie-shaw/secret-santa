package com.secretsanta.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SystemDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public int getCurrentYear() {
        // get the current year
        String SQL = "SELECT attribute_value " +
                       "FROM system.system ";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{}, Integer.class);
    }
}
