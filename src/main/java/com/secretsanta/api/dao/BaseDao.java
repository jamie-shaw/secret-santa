package com.secretsanta.api.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

@Component
public abstract class BaseDao {
    
    @Resource
    JdbcTemplate jdbcTemplate;
    
    @Resource
    SystemContext systemContext;
    
    @Resource
    SessionContext sessionContext;
    
    String getSchema() {
        return sessionContext.getSchema();
    }
    
    int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
    
}
