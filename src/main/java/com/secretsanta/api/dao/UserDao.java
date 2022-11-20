package com.secretsanta.api.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.secretsanta.api.dao.mapper.UserMapper;
import com.secretsanta.api.model.User;

@Component
public class UserDao extends BaseDao {
    
    public enum FilterColumn { USER_NAME, RECIPIENT};
    
    public static final String RECIPIENT = "recipient";
    
    @Resource
    private PasswordEncoder passwordEncoder;
    
    /**
     * Get the requested user
     * 
     * @param currentUser
     * @return
     */
    public User getUser(String username, FilterColumn recipientFilterColumn) {
        
        FilterColumn userFilterColumn = recipientFilterColumn == FilterColumn.RECIPIENT ? FilterColumn.USER_NAME : FilterColumn.RECIPIENT;
                
        String SQL = "SELECT santa_user.user_name, display_name, email " +
                       "FROM " + getSchema() + ".recipient " + 
                 "INNER JOIN " + getSchema() + ".santa_user ON recipient." + userFilterColumn + "= santa_user.user_name " +
                      "WHERE recipient." + recipientFilterColumn + " = ? AND Year = ?";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{username, getCurrentYear()}, new UserMapper());
    }

    public User getUser(String username) {
        
        String SQL = "SELECT user_name, display_name, email " +
                       "FROM " + getSchema() + ".santa_user " + 
                      "WHERE UPPER(user_name) = UPPER(?)";
        
        return jdbcTemplate.queryForObject(SQL, new Object[]{username}, new UserMapper());
    }
    
    public void changePassword(String username, String password) {
        String SQL = "UPDATE " + getSchema() + ".santa_user " + 
                        "SET password = ?, "  +
                           " password_expired = false " +
                      "WHERE user_name = ?";
        
        jdbcTemplate.update(SQL, new Object[] {passwordEncoder.encode(password), username});
    }

    public void resetPasswords(List<String> usernames) {
        if (usernames.size() > 0) {
            String password = passwordEncoder.encode("santa");
            
            String SQL = "UPDATE " + getSchema() + ".santa_user " +
                            "SET password = ?, password_expired = true " +
                          "WHERE user_name = ?";
            
            for (String username : usernames) {
                jdbcTemplate.update(SQL, new Object[] {password, username});
            }
        
        }
    }
    
    public void resetAllPasswords() {
        String SQL = null;
        String password = passwordEncoder.encode("santa");
        
        SQL = "UPDATE " + getSchema() + ".santa_user " +
                 "SET password = ?, password_expired = true ";
        
        jdbcTemplate.update(SQL, new Object[] {password});
    }
    
    public List<User> getAllUsers() {
        String SQL = "SELECT user_name, display_name, email " + 
                       "FROM " + getSchema() + ".santa_user";
        
        return jdbcTemplate.query(SQL, new UserMapper());
    }
}
