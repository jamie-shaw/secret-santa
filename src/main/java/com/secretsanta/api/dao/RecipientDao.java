package com.secretsanta.api.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.secretsanta.api.mapper.RecipientMapper;
import com.secretsanta.api.model.Recipient;

@Component
public class RecipientDao extends BaseDao {
    
    /**
     * Assigns the recipient to the current user
     * 
     * @param currentUser
     * @return
     */
    public void assignRecipient(String currentUser, Recipient recipient) {
        
        // Update recipient record
        String SQL = "UPDATE " + getSchema() + ".recipient " + 
                        "SET assigned = 'Y' " +
                      "WHERE user_name = ? AND year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), getCurrentYear()});
        
        // Update user record
        SQL = "UPDATE " + getSchema() + ".recipient " +
                 "SET recipient = ? " +
               "WHERE user_name = ? AND year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {recipient.getUserName(), currentUser, getCurrentYear()});
    }

    /**
     * @param currentUser
     * @return all recipients that haven't already been assigned
     */
    public List<Recipient> getUnassignedRecipients(String currentUser) {
        
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE assigned = 'N' AND user_name <> ? AND year = ?";
        
        return jdbcTemplate.query(SQL, new Object[] {currentUser, getCurrentYear()}, new RecipientMapper());
    }
    
    public Recipient getRecipientForCurrentUser(String currentUser) {
        // get the recipient for the current user
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE user_name = ? AND year = ?";
        
       return jdbcTemplate.queryForObject(SQL, new Object[] {currentUser, getCurrentYear()}, new RecipientMapper());
    }

    public List<Recipient> getAllRecipients() {
        
        // Get all of the pickers
        String SQL = "SELECT recipient.user_name, recipient, year, assigned " +
                       "FROM " + getSchema() + ".recipient " +
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.user_name = santa_user.user_name " +
                      "WHERE year = ? " +
                   "ORDER BY santa_user.user_name";
        
        return jdbcTemplate.query(SQL, new Object[]{getCurrentYear()}, new RecipientMapper());
    } 
    
    public List<Recipient> getAllRecipientsForCurrentYear() {
        // Get all recipients for the selected year
        String SQL = "SELECT user_name, recipient, year, assigned " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year = ? " +
                   "ORDER BY user_name ASC";
        
        return jdbcTemplate.query(SQL, new Object[] {getCurrentYear()}, new RecipientMapper());
    }

    public List<String> getActiveYears() {
        // Get all of the active years
        String SQL = "SELECT distinct year " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year <> ? " +
                   "ORDER BY year DESC";
        
        return jdbcTemplate.queryForList(SQL, new Object[] {getCurrentYear()}, String.class);
    }
}
