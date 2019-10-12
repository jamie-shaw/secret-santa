package com.secretsanta.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.model.Recipient;

public class RecipientMapper implements RowMapper<Recipient> {
    
    @Override
    public Recipient mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Recipient(
                rs.getString("UserName"),
                rs.getString("Year"),
                rs.getString("Recipient"),
                rs.getString("Assigned").equals("Y")
        );
        
    }
}
