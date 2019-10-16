package com.secretsanta.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.security.SantaUserDetails;

public class UserDetailsMapper implements RowMapper<SantaUserDetails> {

    @Override
    public SantaUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return new SantaUserDetails(
                rs.getString("UserName"),
                rs.getString("Password"),
                rs.getBoolean("PasswordExpired")
        );
    }
}
