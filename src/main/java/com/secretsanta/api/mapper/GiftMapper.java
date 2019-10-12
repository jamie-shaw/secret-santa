package com.secretsanta.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.model.Gift;

public class GiftMapper implements RowMapper<Gift> {

    @Override
    public Gift mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Gift(
                rs.getString("GiftId"),
                rs.getString("UserName"),
                rs.getString("Description"),
                rs.getString("Year")
        );
        
    }
}