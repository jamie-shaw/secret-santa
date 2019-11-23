package com.secretsanta.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.model.Recipient;

public class RecipientMapper implements RowMapper<Recipient> {
    
    @Override
    public Recipient mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Recipient(
                rs.getString("user_name"),
                rs.getString("year"),
                rs.getString("recipient"),
                rs.getBoolean("assigned"),
                rs.getBoolean("viewed")
        );
        
    }
}
