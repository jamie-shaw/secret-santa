package com.secretsanta.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class SantaUserDetailsService implements UserDetailsService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SantaUserDetails userDetails = new SantaUserDetails();
        
        String SQL = "SELECT Recipient " +
                "FROM recipient " + 
               "WHERE UserName = ? AND Year = ?";
 
        String recipient = jdbcTemplate.queryForObject(SQL, new Object[]{"Jamie", "2017"}, String.class);

        return userDetails;
    
    }
}