package com.secretsanta.api.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SystemDao {
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    
    public int getCurrentYear() {
        String SQL = "SELECT attribute_value " +
                       "FROM system.system ";
        
        return jdbcTemplate.queryForObject(SQL, Integer.class);
    }
    
    
    public void setCurrentYear(int year) {
        String SQL = "UPDATE system.system " +
                     "   SET attribute_value = ?";
        
        jdbcTemplate.update(SQL, year);
    }

}
