package com.secretsanta.api.dao;

import org.springframework.stereotype.Component;

import com.secretsanta.api.mapper.UserMapper;
import com.secretsanta.api.model.User;

@Component
public class UserDao extends BaseDao{
    
    /**
     * Get the requested user
     * 
     * @param currentUser
     * @return
     */
    public User getUser(String username, String filterColumn) {
        
        String SQL = "SELECT santa_user.user_name, display_name, email " +
                       "FROM " + getSchema() + ".recipient " + 
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.recipient = santa_user.user_name " +
                      "WHERE recipient." + filterColumn + " = ? AND Year = ?";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{username, getCurrentYear()}, new UserMapper());
    }
    
}
