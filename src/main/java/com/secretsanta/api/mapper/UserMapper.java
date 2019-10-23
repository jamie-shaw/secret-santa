package com.secretsanta.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.model.User;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        User user = new User(rs.getString("user_name"));
        user.setDisplayName(rs.getString("display_name"));
        user.setEmail(rs.getString("email"));
        
        return user;
    }
}
