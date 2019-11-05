package com.secretsanta.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

@Component
public abstract class BaseDao {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    SystemContext systemContext;
    
    @Autowired
    SessionContext sessionContext;
    
    String getSchema() {
        return sessionContext.getSchema();
    }
    
    int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
    
}
