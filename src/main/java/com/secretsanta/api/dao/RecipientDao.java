package com.secretsanta.api.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.secretsanta.api.dao.mapper.RecipientMapper;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

@Component
public class RecipientDao extends BaseDao {
    
    @Autowired
    UserDao userDao;
    
    /**
     * Assigns the recipient to the current user
     * 
     * @param currentUser
     * @return
     */
    public void assignRecipient(String currentUser, Recipient recipient) {
        
        // Update recipient record
        String SQL = "UPDATE " + getSchema() + ".recipient " + 
                        "SET assigned = true " +
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
                      "WHERE assigned = false AND user_name <> ? AND year = ?";
        
        return jdbcTemplate.query(SQL, new Object[] {currentUser, getCurrentYear()}, new RecipientMapper());
    }
    
    public Recipient getRecipientForCurrentUser(String currentUser) {
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE user_name = ? AND year = ?";
        
       return jdbcTemplate.queryForObject(SQL, new Object[] {currentUser, getCurrentYear()}, new RecipientMapper());
    }

    public List<Recipient> getAllRecipients() {
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient.user_name = santa_user.user_name " +
                      "WHERE year = ? " +
                   "ORDER BY santa_user.user_name";
        
        return jdbcTemplate.query(SQL, new Object[]{getCurrentYear()}, new RecipientMapper());
    } 
    
    public List<Recipient> getAllRecipientsForSelectedYear(int selectedYear) {
        String SQL = "SELECT * " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year = ? " +
                   "ORDER BY user_name ASC";
        
        return jdbcTemplate.query(SQL, new Object[] {selectedYear}, new RecipientMapper());
    }

    public List<String> getActiveYears() {
        String SQL = "SELECT distinct year " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year <> ? " +
                   "ORDER BY year DESC";
        
        return jdbcTemplate.queryForList(SQL, new Object[] {getCurrentYear()}, String.class);
    }
    
    public int getSelfAssignedRecipients() {
        String SQL = "SELECT count(*) " +
                       "FROM " + getSchema() + ".recipient " +
                      "WHERE year = ? " +
                        "AND user_name = recipient";
        
        return jdbcTemplate.queryForObject(SQL, new Object[] {getCurrentYear()}, Integer.class);
    }

    public void clearPicks() {
        String SQL = "UPDATE " + getSchema() + ".recipient " +
                        "SET recipient = '', assigned = false " +
                      "WHERE year = ?";
       
       jdbcTemplate.update(SQL, new Object[] {getCurrentYear()});
        
    }

    public void processPick(String currentUser) {
        String SQL = "UPDATE " + getSchema() + ".recipient " + 
                        "SET viewed = true " +
                      "WHERE user_name = ? AND year = ?";
        
        jdbcTemplate.update(SQL, new Object[] {currentUser, getCurrentYear()});
    }
    
    public void resetAllRecipients() {
        List<User> users = userDao.getAllUsers();
        
        for (User user : users) {
            createRecipient(user);
        }
    }
    
    private void createRecipient(User user) {
        String SQL =  "INSERT INTO " + getSchema() + ".recipient " +
                      "VALUES(?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(SQL, new Object[] {user.getUsername(), getCurrentYear(), "", false, false});
    }

}
