package com.secretsanta.api.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.security.SantaUserDetails;

public class UserDetailsMapper implements RowMapper<SantaUserDetails> {

    @Override
    public SantaUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return new SantaUserDetails(
                rs.getString("user_name"),
                rs.getString("password"),
                rs.getBoolean("password_expired")
        );
    }
}
