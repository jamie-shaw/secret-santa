package com.secretsanta.api.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.secretsanta.api.model.RequestContext;
import com.secretsanta.api.model.SystemContext;

import jakarta.annotation.Resource;

@Component
public abstract class BaseDao {
    
    @Resource
    JdbcTemplate jdbcTemplate;
    
    @Resource
    SystemContext systemContext;
    
    String getSchema() {
        return RequestContext.getSchema();
    }
    
    int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
    
}
