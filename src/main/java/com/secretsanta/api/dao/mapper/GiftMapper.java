package com.secretsanta.api.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secretsanta.api.model.Gift;

public class GiftMapper implements RowMapper<Gift> {

    @Override
    public Gift mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Gift(
                rs.getString("gift_id"),
                rs.getString("user_name"),
                rs.getString("description"),
                rs.getString("link"),
                rs.getString("year")
        );
        
    }
}