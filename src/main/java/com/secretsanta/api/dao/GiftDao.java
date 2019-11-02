package com.secretsanta.api.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.secretsanta.api.mapper.GiftMapper;
import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

@Component
public class GiftDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    SystemContext systemContext;
    
    @Autowired
    private SessionContext sessionContext;
    
    /**
     * Get the list of gift ideas a recipient is suggesting to their Santa
     * 
     * @param currentUser
     * @return
     */
    public List<Gift> getIdeasForSanta(String currentUser) {
        
        String SQL = "SELECT gift_id, description, user_name, year " + 
                     "  FROM " + getSchema() + ".gift " + 
                     " WHERE user_name = ? AND year = ?";
        
        return jdbcTemplate.query(SQL, new Object[]{currentUser, getCurrentYear()}, new GiftMapper());
    }
    
    
    /**
     * Get the list of gift ideas a Santa is suggesting to the recipient
     * 
     * @param recipientUserName
     * @return
     */
    public List<Gift> getIdeasFromSanta(String recipientUserName) {
        
        String SQL = "SELECT gift_id, user_name, description, year " +
                       "FROM " + getSchema() + ".gift " +
                      "WHERE user_name = ? AND year = ?";
        
        return jdbcTemplate.query(SQL, new Object[]{recipientUserName, getCurrentYear()}, new GiftMapper());
    }
    
    /**
     * @param giftId
     * @return
     */
    public Gift getGiftDetail(int giftId) {
        
        String SQL =  "SELECT gift_id, description, user_name, year " +
                        "FROM" + getSchema() + ".gift " +
                       "WHERE gift_id = ?";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{giftId}, new GiftMapper());
    }
    
    /**
     * @param gift
     * @param currentUser
     * @param model
     * @return
     */
    public void createGift(String currentUser, String description) {
        
        String SQL =  "INSERT INTO " + getSchema() + ".gift " +
                      "VALUES(DEFAULT, ?, ?, ?)";
        
        jdbcTemplate.update(SQL, new Object[]{currentUser, description, getCurrentYear()});
    }
    
    /**
     * @param giftId
     * @param description
     * @return
     */
    public void updateGift(int giftId, String description) {
        
        String SQL = "UPDATE " + getSchema() + ".gift " + 
                        "SET description = ? " +
                      "WHERE gift_id = ?";
        
        jdbcTemplate.update(SQL, new Object[]{description, giftId});
    }
    
    /**
     * @param giftId
     */
    public void deleteGift(int giftId) {
        
        String SQL =  "DELETE " +
                        "FROM " + getSchema() + ".gift " +
                       "WHERE gift_id = ?";
        
        jdbcTemplate.update(SQL, new Object[]{giftId});
    }
    
    private String getSchema() {
        return sessionContext.getSchema();
    }
    
    private int getCurrentYear() {
        return systemContext.getCurrentYear();
    }
    
}
